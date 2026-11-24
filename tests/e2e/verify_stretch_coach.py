#!/usr/bin/env python3
"""
Opaque-box E2E Verification Suite for Stretch Coach (Python 3.9+ stdlib only).

Supports:
  --phase m1    : Catalog, legacy drawable removal, JPEG 1376x768 specs,
                  ArtworkMapper + ArtworkBatch1..4 integrity, L/R pair symmetry.
  --phase final : All --phase m1 checks PLUS full stretch_gallery.html verification.
"""

from __future__ import annotations

import argparse
import dataclasses
import html.parser
import pathlib
import re
import struct
import sys
from typing import Dict, List, Optional, Tuple


LEGACY_15_STEMS = {
    "img_stretch_butterfly",
    "img_stretch_calf",
    "img_stretch_cat_cow",
    "img_stretch_chest",
    "img_stretch_cobra",
    "img_stretch_figure_four",
    "img_stretch_floor",
    "img_stretch_hamstring",
    "img_stretch_lunge",
    "img_stretch_pigeon",
    "img_stretch_quad",
    "img_stretch_side_bend",
    "img_stretch_twist",
    "img_stretch_upper",
    "img_stretch_wrist",
}

EXPECTED_LR_PAIRS: List[Tuple[str, str]] = [
    # StretchLibrary (22 pairs)
    ("neck_side_right", "neck_side_left"),
    ("cross_body_shoulder_right", "cross_body_shoulder_left"),
    ("thread_the_needle_right", "thread_the_needle_left"),
    ("reclining_spinal_twist_right", "reclining_spinal_twist_left"),
    ("hip_flexor_lunge_right", "hip_flexor_lunge_left"),
    ("figure_four_glute_right", "figure_four_glute_left"),
    ("pigeon_pose_right", "pigeon_pose_left"),
    ("standing_quad_right", "standing_quad_left"),
    ("wall_calf_gastrocnemius_right", "wall_calf_gastrocnemius_left"),
    ("trikonasana_triangle_right", "trikonasana_triangle_left"),
    ("vrikshasana_tree_right", "vrikshasana_tree_left"),
    ("janu_sirsasana_right", "janu_sirsasana_left"),
    ("ardha_matsyendrasana_right", "ardha_matsyendrasana_left"),
    ("gomukhasana_arms_right", "gomukhasana_arms_left"),
    ("virabhadrasana_one_right", "virabhadrasana_one_left"),
    ("virabhadrasana_two_right", "virabhadrasana_two_left"),
    ("anjaneyasana_low_lunge_right", "anjaneyasana_low_lunge_left"),
    ("supta_padangusthasana_right", "supta_padangusthasana_left"),
    ("garudasana_eagle_pose_right", "garudasana_eagle_pose_left"),
    ("parsvottanasana_pyramid_right", "parsvottanasana_pyramid_left"),
    ("eka_pada_rajakapotasana_pigeon_right", "eka_pada_rajakapotasana_pigeon_left"),
    ("supta_matsyendrasana_twist_right", "supta_matsyendrasana_twist_left"),
    # ExtraYogaAsanas (10 pairs)
    ("ardha_chandrasana_right", "ardha_chandrasana_left"),
    ("utthita_parsvakonasana_right", "utthita_parsvakonasana_left"),
    ("parivrtta_trikonasana_right", "parivrtta_trikonasana_left"),
    ("hanumanasana_right", "hanumanasana_left"),
    ("natarajasana_right", "natarajasana_left"),
    ("vasisthasana_right", "vasisthasana_left"),
    ("skandasana_right", "skandasana_left"),
    ("bharadvajasana_right", "bharadvajasana_left"),
    ("parighasana_gate_right", "parighasana_gate_left"),
    ("virabhadrasana_3_right", "virabhadrasana_3_left"),
]

DEVANAGARI_RE = re.compile(r"[\u0900-\u097F]")
WHITESPACE_RE = re.compile(r"\s+")
VALID_DRAWABLE_FILENAME_RE = re.compile(r"^img_stretch_[a-z0-9_]+\.jpg$")


@dataclasses.dataclass
class StretchEntry:
    index: int
    source: str
    id: str
    name: str
    category: str
    prep_seconds: int
    hold_seconds: int
    prep_cue: str
    hold_cue: str
    benefits: str
    explicit_indian_name: Optional[str]
    resolved_indian_name: Optional[str] = None


def normalize_key(raw: str) -> str:
    return WHITESPACE_RE.sub(" ", raw.strip().lower())


def inspect_jpeg(path: pathlib.Path) -> Tuple[int, int, int]:
    """
    Pure-Python JPEG binary inspector.
    Verifies SOI (FF D8 FF), EOI (FF D9), extracts SOF marker type & (width, height),
    and enforces baseline JPEG (SOF0 = 0xC0) with exact 1376x768 dimensions.
    """
    if not path.is_file():
        raise AssertionError(f"JPEG file does not exist: {path}")
    data = path.read_bytes()
    if len(data) < 16:
        raise AssertionError(f"JPEG file too small ({len(data)} bytes): {path}")
    if data[:3] != b"\xff\xd8\xff":
        raise AssertionError(f"Invalid JPEG SOI header in {path}: {data[:4].hex()}")
    if data[-2:] != b"\xff\xd9":
        raise AssertionError(f"Missing JPEG EOI (FF D9) trailer in {path}: {data[-4:].hex()}")

    pos = 2
    n = len(data)
    sof_type: Optional[int] = None
    width: Optional[int] = None
    height: Optional[int] = None

    while pos < n:
        if data[pos] != 0xFF:
            raise AssertionError(f"Corrupt JPEG marker alignment at offset {pos} in {path}")
        while pos < n and data[pos] == 0xFF:
            pos += 1
        if pos >= n:
            break
        marker = data[pos]
        pos += 1

        if marker in (0x00, 0x01, 0xD8, 0xD9) or (0xD0 <= marker <= 0xD7):
            if marker == 0xD9:
                break
            continue

        if pos + 2 > n:
            raise AssertionError(f"Truncated JPEG segment length at offset {pos} in {path}")
        seg_len = struct.unpack(">H", data[pos : pos + 2])[0]
        if seg_len < 2 or pos + seg_len > n:
            raise AssertionError(f"Invalid JPEG segment length {seg_len} at offset {pos} in {path}")

        if marker in (0xC0, 0xC1, 0xC2):
            if seg_len < 7:
                raise AssertionError(f"Truncated SOF segment in {path}")
            sof_type = marker
            height, width = struct.unpack(">HH", data[pos + 3 : pos + 7])
            break

        if marker == 0xDA:  # Start of Scan
            break
        pos += seg_len

    if sof_type is None or width is None or height is None:
        raise AssertionError(f"No SOF frame header found in JPEG: {path}")
    if sof_type != 0xC0:
        raise AssertionError(f"Expected baseline JPEG (SOF0=0xC0), got 0x{sof_type:02X} in {path}")
    if (width, height) != (1376, 768):
        raise AssertionError(f"Expected 1376x768 unified JPEG, got {width}x{height} in {path}")

    return sof_type, width, height


def _find_matching_paren(text: str, open_idx: int) -> int:
    depth = 0
    in_string = False
    escape = False
    i = open_idx
    n = len(text)
    while i < n:
        ch = text[i]
        if in_string:
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_string = False
        else:
            if ch == '"':
                in_string = True
            elif ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
                if depth == 0:
                    return i
        i += 1
    raise AssertionError(f"Unclosed parenthesis starting at offset {open_idx}")


def _unescape_kotlin_string(raw: str) -> str:
    return (
        raw.replace('\\"', '"')
        .replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
    )


def _extract_named_string(block: str, field: str) -> Optional[str]:
    pattern = re.compile(rf'\b{re.escape(field)}\s*=\s*"((?:[^"\\]|\\.)*)"')
    m = pattern.search(block)
    return _unescape_kotlin_string(m.group(1)) if m else None


def _extract_named_int(block: str, field: str) -> int:
    m = re.search(rf"\b{re.escape(field)}\s*=\s*(\d+)", block)
    if not m:
        raise AssertionError(f"Missing integer field '{field}' in block: {block[:120]}")
    return int(m.group(1))


def _extract_named_category(block: str) -> str:
    m = re.search(r"\bcategory\s*=\s*StretchCategory\.([A-Z0-9_]+)", block)
    if not m:
        raise AssertionError(f"Missing StretchCategory in block: {block[:120]}")
    return m.group(1)


def parse_library_stretches_from_file(
    kt_path: pathlib.Path, source_label: str, category_titles: Dict[str, str], start_index: int
) -> List[StretchEntry]:
    text = kt_path.read_text(encoding="utf-8")
    entries: List[StretchEntry] = []
    idx = start_index
    for m in re.finditer(r"LibraryStretch\s*\(", text):
        # Skip the data class definition itself
        prefix = text[max(0, m.start() - 20) : m.start()]
        if "data class" in prefix:
            continue
        open_paren = m.end() - 1
        close_paren = _find_matching_paren(text, open_paren)
        block = text[open_paren + 1 : close_paren]
        enum_key = _extract_named_category(block)
        entries.append(
            StretchEntry(
                index=idx,
                source=source_label,
                id=_extract_named_string(block, "id") or "",
                name=_extract_named_string(block, "name") or "",
                category=category_titles[enum_key],
                prep_seconds=_extract_named_int(block, "defaultPrepSeconds"),
                hold_seconds=_extract_named_int(block, "defaultHoldSeconds"),
                prep_cue=_extract_named_string(block, "prepCue") or "",
                hold_cue=_extract_named_string(block, "holdCue") or "",
                benefits=_extract_named_string(block, "benefits") or "",
                explicit_indian_name=_extract_named_string(block, "indianName"),
            )
        )
        idx += 1
    return entries


def parse_category_titles(stretch_library_path: pathlib.Path) -> Dict[str, str]:
    text = stretch_library_path.read_text(encoding="utf-8")
    m = re.search(r"enum class StretchCategory[^{]*\{(.*?)\}", text, re.DOTALL)
    if not m:
        raise AssertionError("Could not locate StretchCategory enum in StretchLibrary.kt")
    titles: Dict[str, str] = {}
    for item in re.finditer(r'([A-Z0-9_]+)\s*\(\s*"([^"]+)"\s*\)', m.group(1)):
        titles[item.group(1)] = item.group(2)
    return titles


class SanskritDevanagariResolver:
    """Reproduces SanskritPhonetics.buildSpokenPlan(name).sanskritDevanagari."""

    def __init__(self, phonetics_path: pathlib.Path) -> None:
        text = phonetics_path.read_text(encoding="utf-8")

        # Parse SANSKRIT_TO_ENGLISH_MAP keys
        eng_block_m = re.search(
            r"SANSKRIT_TO_ENGLISH_MAP\s*=\s*mapOf\s*\((.*?)\n\s*\)", text, re.DOTALL
        )
        if not eng_block_m:
            raise AssertionError("Could not parse SANSKRIT_TO_ENGLISH_MAP in SanskritPhonetics.kt")
        self.english_keys = [
            m.group(1).lower()
            for m in re.finditer(r'"([^"]+)"\s+to\s+"[^"]+"', eng_block_m.group(1))
        ]

        # Parse SANSKRIT_TO_DEVANAGARI_MAP regex -> Devanagari pairs in exact order
        dev_block_m = re.search(
            r"SANSKRIT_TO_DEVANAGARI_MAP\s*=\s*listOf\s*\((.*?)\n\s*\)", text, re.DOTALL
        )
        if not dev_block_m:
            raise AssertionError("Could not parse SANSKRIT_TO_DEVANAGARI_MAP in SanskritPhonetics.kt")
        self.devanagari_rules: List[Tuple[re.Pattern[str], str]] = []
        for rm in re.finditer(
            r'Regex\("((?:[^"\\]|\\.)*)"\)\s+to\s+"([^"]+)"', dev_block_m.group(1)
        ):
            raw_pat = rm.group(1).replace("\\\\", "\\")
            # Strip Kotlin (?i) inline flag for explicit re.IGNORECASE
            pat = raw_pat.replace("(?i)", "")
            self.devanagari_rules.append((re.compile(pat, re.IGNORECASE), rm.group(2)))

    def _is_sanskrit_term(self, text: str) -> bool:
        lower = text.strip().lower()
        if not lower:
            return False
        if DEVANAGARI_RE.search(lower):
            return True
        if (
            lower.endswith("asana")
            or lower.endswith("asan")
            or "asana " in lower
            or "asan " in lower
            or "karani" in lower
            or "namaskar" in lower
            or "pranayama" in lower
            or "mudra" in lower
            or "bandha" in lower
        ):
            return True
        return any(k in lower for k in self.english_keys)

    @staticmethod
    def _transliterate_word_to_devanagari(raw_word: str) -> str:
        word = re.sub(r"[^a-z]", "", raw_word.lower())
        if not word:
            return raw_word
        if len(word) > 5 and word.endswith("asana"):
            stem, suffix = word[:-5], "आसन"
        elif len(word) > 4 and word.endswith("asan"):
            stem, suffix = word[:-4], "आसन"
        else:
            stem, suffix = word, ""

        consonants = [
            ("ksh", "क्ष"), ("kh", "ख"), ("gh", "घ"), ("ch", "च"), ("jh", "झ"),
            ("th", "थ"), ("dh", "ध"), ("ph", "फ"), ("bh", "भ"), ("sh", "श"),
            ("k", "क"), ("g", "ग"), ("c", "च"), ("j", "ज"), ("t", "त"), ("d", "द"),
            ("n", "न"), ("p", "प"), ("f", "फ"), ("b", "ब"), ("m", "म"), ("y", "य"),
            ("r", "र"), ("l", "ल"), ("v", "व"), ("w", "व"), ("s", "स"), ("h", "ह"),
        ]
        ind_vowels = [
            ("aa", "आ"), ("ee", "ई"), ("oo", "ऊ"), ("ai", "ऐ"), ("au", "औ"),
            ("a", "अ"), ("i", "इ"), ("u", "उ"), ("e", "ए"), ("o", "ओ"),
        ]
        matras = [
            ("aa", "ा"), ("ee", "ी"), ("oo", "ू"), ("ai", "ै"), ("au", "ौ"),
            ("a", ""), ("i", "ि"), ("u", "ु"), ("e", "े"), ("o", "ो"),
        ]

        out: List[str] = []
        i = 0
        last_was_consonant = False
        while i < len(stem):
            rem = stem[i:]
            matched_c = next(((k, v) for k, v in consonants if rem.startswith(k)), None)
            if matched_c is not None:
                if last_was_consonant:
                    out.append("्")
                out.append(matched_c[1])
                i += len(matched_c[0])
                last_was_consonant = True
            else:
                vmap = matras if last_was_consonant else ind_vowels
                matched_v = next(((k, v) for k, v in vmap if rem.startswith(k)), None)
                if matched_v is not None:
                    out.append(matched_v[1])
                    i += len(matched_v[0])
                    last_was_consonant = False
                else:
                    i += 1
                    last_was_consonant = False

        if suffix:
            if last_was_consonant:
                out.append("ासन")
            else:
                out.append(suffix)
        return "".join(out)

    def to_devanagari(self, sanskrit_term: str) -> str:
        trimmed = sanskrit_term.strip()
        if not trimmed:
            return ""
        if DEVANAGARI_RE.search(trimmed):
            return trimmed
        result = trimmed
        matched_curated = False
        for regex, dev in self.devanagari_rules:
            if regex.search(result):
                result = regex.sub(dev, result)
                matched_curated = True
        if matched_curated and not re.search(r"[a-zA-Z]", result):
            return result
        parts = []
        for word in re.split(r"\s+", result):
            if DEVANAGARI_RE.search(word):
                parts.append(word)
            else:
                parts.append(self._transliterate_word_to_devanagari(word))
        return " ".join(parts)

    def resolve_indian_name(self, raw_name: str, explicit_indian_name: Optional[str]) -> Optional[str]:
        if explicit_indian_name:
            return explicit_indian_name
        cleaned = raw_name.strip()
        right_brackets = ["(Right)", "(Right Leg)", "(Right Shin Forward)", "(Right Forward)", "(Right Leg Back)", "(Right Side)"]
        left_brackets = ["(Left)", "(Left Leg)", "(Left Shin Forward)", "(Left Forward)", "(Left Leg Back)", "(Left Side)"]
        side_patterns = [
            re.compile(r"[-\s]*\b(right|right side|right leg|right shin forward|right forward|right leg back)\b", re.IGNORECASE),
            re.compile(r"[-\s]*\b(left|left side|left leg|left shin forward|left forward|left leg back)\b", re.IGNORECASE),
        ]

        side_found = False
        for br in right_brackets:
            if br.lower() in cleaned.lower():
                side_found = True
                cleaned = re.sub(re.escape(br), "", cleaned, flags=re.IGNORECASE).strip()
        if not side_found:
            for br in left_brackets:
                if br.lower() in cleaned.lower():
                    side_found = True
                    cleaned = re.sub(re.escape(br), "", cleaned, flags=re.IGNORECASE).strip()

        bm = re.search(r"(.*?)\((.*?)\)", cleaned)
        outer_part = bm.group(1).strip() if bm else cleaned
        inner_part = bm.group(2).strip() if bm else ""

        if not side_found:
            for pat in side_patterns:
                if pat.search(inner_part):
                    inner_part = pat.sub("", inner_part).strip()
                    break
                if pat.search(outer_part):
                    outer_part = pat.sub("", outer_part).strip()
                    break
        else:
            for pat in side_patterns:
                inner_part = pat.sub("", inner_part).strip()
                outer_part = pat.sub("", outer_part).strip()

        if inner_part and self._is_sanskrit_term(inner_part):
            sanskrit_candidate = inner_part
        elif self._is_sanskrit_term(outer_part):
            sanskrit_candidate = outer_part
        else:
            sanskrit_candidate = ""

        return self.to_devanagari(sanskrit_candidate) if sanskrit_candidate else None


def _strip_kotlin_comments(text: str) -> str:
    """
    Strips Kotlin // line comments and /* ... */ (including nested) block comments
    while preserving string/raw-string/char literals and newlines.
    """
    out: List[str] = []
    i = 0
    n = len(text)
    while i < n:
        ch = text[i]
        # Raw triple-quoted string literal """..."""
        if text.startswith('"""', i):
            end = text.find('"""', i + 3)
            if end == -1:
                out.append(text[i:])
                break
            out.append(text[i : end + 3])
            i = end + 3
        # Regular string literal "..."
        elif ch == '"':
            j = i + 1
            while j < n:
                if text[j] == "\\":
                    j += 2
                    continue
                if text[j] == '"':
                    j += 1
                    break
                j += 1
            out.append(text[i:j])
            i = j
        # Char literal '...'
        elif ch == "'":
            j = i + 1
            while j < n:
                if text[j] == "\\":
                    j += 2
                    continue
                if text[j] == "'":
                    j += 1
                    break
                j += 1
            out.append(text[i:j])
            i = j
        # Line comment //...
        elif text.startswith("//", i):
            nl = text.find("\n", i + 2)
            if nl == -1:
                break
            out.append("\n")
            i = nl + 1
        # Block comment /* ... */ (supports Kotlin nesting)
        elif text.startswith("/*", i):
            out.append(" ")
            depth = 1
            j = i + 2
            while j < n and depth > 0:
                if text.startswith("/*", j):
                    depth += 1
                    j += 2
                elif text.startswith("*/", j):
                    depth -= 1
                    j += 2
                else:
                    if text[j] == "\n":
                        out.append("\n")
                    j += 1
            i = j
        else:
            out.append(ch)
            i += 1
    return "".join(out)


def parse_artwork_mappings(root: pathlib.Path) -> Dict[str, str]:
    """
    Parses ArtworkMapper.kt and ArtworkBatch1..4.kt, checks guardrails,
    and returns normalized_key -> drawable_stem map.
    """
    mapper_path = root / "app/src/main/java/com/example/data/ArtworkMapper.kt"
    mapper_text = _strip_kotlin_comments(mapper_path.read_text(encoding="utf-8"))

    # Verify nullable return type Int?
    fn_m = re.search(r"fun\s+getDrawableForStretch\s*\(", mapper_text)
    if not fn_m:
        raise AssertionError("ArtworkMapper.getDrawableForStretch function missing")
    close_paren = _find_matching_paren(mapper_text, fn_m.end() - 1)
    ret_m = re.match(r"\s*:\s*([A-Za-z0-9_?]+)", mapper_text[close_paren + 1 :])
    if not ret_m or ret_m.group(1) != "Int?":
        raise AssertionError(
            f"ArtworkMapper.getDrawableForStretch must return 'Int?', found: {ret_m.group(1) if ret_m else 'missing'}"
        )

    # Verify no category fallback or catch-all fallback
    if "c.contains(" in mapper_text or ".contains(" in mapper_text or re.search(r"else\s*->\s*R\.drawable\.", mapper_text):
        raise AssertionError("ArtworkMapper.kt must not contain category or catch-all fallbacks")

    # Verify PlayerArtworkCard does not contain hardcoded standing fallback
    player_card_path = root / "app/src/main/java/com/example/ui/components/PlayerArtworkCard.kt"
    player_card_text = _strip_kotlin_comments(player_card_path.read_text(encoding="utf-8"))
    if "R.drawable.img_stretch_standing" in player_card_text:
        raise AssertionError("PlayerArtworkCard.kt must not contain hardcoded R.drawable.img_stretch_standing fallback")

    source_files = [
        mapper_path,
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch1.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch2.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch3.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch4.kt",
    ]

    pair_re = re.compile(r'"((?:[^"\\]|\\.)*)"\s+to\s+R\.drawable\.([a-z0-9_]+)', re.DOTALL)
    merged_map: Dict[str, str] = {}

    for src in source_files:
        if not src.is_file():
            raise AssertionError(f"Missing required artwork registry file: {src}")
        content = _strip_kotlin_comments(src.read_text(encoding="utf-8"))
        if "c.contains(" in content or ".contains(" in content or re.search(r"else\s*->\s*R\.drawable\.", content):
            raise AssertionError(f"Fallback pattern forbidden in {src}")

        for m in pair_re.finditer(content):
            raw_key = _unescape_kotlin_string(m.group(1))
            drawable_stem = m.group(2)
            norm = normalize_key(raw_key)
            if raw_key != norm:
                raise AssertionError(
                    f"Artwork key in {src.name} must be normalized lowercase single-spaced: '{raw_key}' != '{norm}'"
                )
            if drawable_stem in LEGACY_15_STEMS:
                raise AssertionError(f"Legacy drawable '{drawable_stem}' referenced in {src}")
            if not drawable_stem.startswith("img_stretch_"):
                raise AssertionError(f"Invalid stretch drawable name '{drawable_stem}' in {src}")
            if norm in merged_map and merged_map[norm] != drawable_stem:
                raise AssertionError(
                    f"Conflicting mapping for key '{norm}': '{merged_map[norm]}' vs '{drawable_stem}'"
                )
            merged_map[norm] = drawable_stem

    return merged_map


class GalleryHTMLParser(html.parser.HTMLParser):
    """Extracts all .stretch-card article records from stretch_gallery.html."""

    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self.cards: List[Dict[str, object]] = []
        self._stack: List[Tuple[str, bool]] = []
        self._current: Optional[Dict[str, object]] = None

    def handle_starttag(self, tag: str, attrs: List[Tuple[str, Optional[str]]]) -> None:
        attr_map = {k: (v or "") for k, v in attrs}
        classes = attr_map.get("class", "").split()
        is_card_root = self._current is None and ("stretch-card" in classes or "data-index" in attr_map)

        if is_card_root:
            self._current = {
                "tag": tag,
                "attrs": attr_map,
                "imgs": [],
                "links": [],
                "text_parts": [],
            }
            self._stack.append((tag, True))
        elif self._current is not None:
            self._stack.append((tag, False))
            if tag == "img":
                self._current["imgs"].append(attr_map)
            elif tag == "a":
                self._current["links"].append(attr_map)

    def handle_startendtag(self, tag: str, attrs: List[Tuple[str, Optional[str]]]) -> None:
        attr_map = {k: (v or "") for k, v in attrs}
        if self._current is not None:
            if tag == "img":
                self._current["imgs"].append(attr_map)
            elif tag == "a":
                self._current["links"].append(attr_map)

    def handle_data(self, data: str) -> None:
        if self._current is not None:
            self._current["text_parts"].append(data)

    def handle_endtag(self, tag: str) -> None:
        if not self._stack:
            return
        # Pop until matching tag if malformed nesting occurs
        while self._stack:
            popped_tag, is_root = self._stack.pop()
            if is_root and self._current is not None:
                self._current["text"] = WHITESPACE_RE.sub(" ", "".join(self._current["text_parts"])).strip()
                self.cards.append(self._current)
                self._current = None
                break
            if popped_tag == tag:
                break


def verify_gallery_html(
    root: pathlib.Path,
    catalog: List[StretchEntry],
    mapped_by_stretch_id: Dict[str, Optional[str]],
) -> Tuple[int, int]:
    gallery_path = root / "stretch_gallery.html"
    if not gallery_path.is_file():
        raise AssertionError(f"Missing M6 gallery file: {gallery_path}")

    html_text = gallery_path.read_text(encoding="utf-8")
    parser = GalleryHTMLParser()
    parser.feed(html_text)
    parser.close()

    cards = parser.cards
    if len(cards) != 150:
        raise AssertionError(f"stretch_gallery.html must contain exactly 150 stretch cards, found {len(cards)}")

    mapped_cards = 0
    unmapped_cards = 0

    for idx, (card, stretch) in enumerate(zip(cards, catalog), start=1):
        attrs: Dict[str, str] = card["attrs"]  # type: ignore[assignment]
        text: str = card["text"]  # type: ignore[assignment]
        imgs: List[Dict[str, str]] = card["imgs"]  # type: ignore[assignment]

        card_index = attrs.get("data-index")
        card_id = attrs.get("data-id")
        if card_index != str(idx):
            raise AssertionError(f"Card #{idx} has wrong data-index='{card_index}' (expected '{idx}')")
        if card_id != stretch.id:
            raise AssertionError(f"Card #{idx} has wrong data-id='{card_id}' (expected '{stretch.id}')")
        if stretch.name not in text:
            raise AssertionError(f"Card #{idx} ({stretch.id}) does not display stretch name '{stretch.name}'")

        if stretch.resolved_indian_name:
            if stretch.resolved_indian_name not in text:
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) missing Indian/Sanskrit Devanagari name '{stretch.resolved_indian_name}'"
                )

        expected_stem = mapped_by_stretch_id[stretch.id]
        if expected_stem is not None:
            mapped_cards += 1
            expected_filename = f"{expected_stem}.jpg"
            expected_rel = f"app/src/main/res/drawable/{expected_filename}"
            if attrs.get("data-status") != "mapped":
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) expected data-status='mapped', got '{attrs.get('data-status')}'"
                )
            if attrs.get("data-drawable") != expected_filename:
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) expected data-drawable='{expected_filename}', got '{attrs.get('data-drawable')}'"
                )
            if len(imgs) != 1:
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) expected exactly 1 <img> tag, found {len(imgs)}"
                )
            img_src = imgs[0].get("src", "")
            if img_src != expected_rel:
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) expected img src='{expected_rel}', got '{img_src}'"
                )
            if img_src.startswith("http://") or img_src.startswith("https://"):
                raise AssertionError(f"Card #{idx} uses external image URL: {img_src}")
            if any(leg in img_src for leg in LEGACY_15_STEMS):
                raise AssertionError(f"Card #{idx} references legacy drawable: {img_src}")
            img_file = root / img_src
            inspect_jpeg(img_file)
        else:
            unmapped_cards += 1
            if attrs.get("data-status") != "unmapped":
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) expected data-status='unmapped', got '{attrs.get('data-status')}'"
                )
            if attrs.get("data-drawable", "") != "":
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) unmapped card must have empty data-drawable, got '{attrs.get('data-drawable')}'"
                )
            if imgs:
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) unmapped card must contain 0 <img> tags, found {len(imgs)}"
                )
            if "No verified image assigned" not in text:
                raise AssertionError(
                    f"Card #{idx} ({stretch.id}) unmapped card must explicitly state 'No verified image assigned'"
                )

    return mapped_cards, unmapped_cards


def run_verification(root: pathlib.Path, phase: str) -> None:
    stretch_lib_path = root / "app/src/main/java/com/example/data/StretchLibrary.kt"
    extra_asanas_path = root / "app/src/main/java/com/example/data/ExtraYogaAsanas.kt"
    phonetics_path = root / "app/src/main/java/com/example/audio/SanskritPhonetics.kt"
    res_dir = root / "app/src/main/res"
    drawable_dir = res_dir / "drawable"

    # -------------------------------------------------------------------------
    # Tier 1: Catalog Completeness & Devanagari Resolution
    # -------------------------------------------------------------------------
    category_titles = parse_category_titles(stretch_lib_path)
    first_100 = parse_library_stretches_from_file(stretch_lib_path, "StretchLibrary", category_titles, 1)
    extra_50 = parse_library_stretches_from_file(extra_asanas_path, "ExtraYogaAsanas", category_titles, 101)

    if len(first_100) != 100:
        raise AssertionError(f"Expected 100 items in StretchLibrary.kt, parsed {len(first_100)}")
    if len(extra_50) != 50:
        raise AssertionError(f"Expected 50 items in ExtraYogaAsanas.kt, parsed {len(extra_50)}")

    catalog = first_100 + extra_50
    if len({s.id for s in catalog}) != 150:
        raise AssertionError("Duplicate stretch IDs found in 150-item catalog")
    if len({s.name.strip() for s in catalog}) != 150:
        raise AssertionError("Duplicate trimmed stretch names found in 150-item catalog")

    for s in catalog:
        if not s.id or not s.name or not s.prep_cue.strip() or not s.hold_cue.strip() or not s.benefits.strip():
            raise AssertionError(f"Blank metadata/cues on stretch {s.id}")
        if s.prep_seconds <= 0 or s.hold_seconds <= 0:
            raise AssertionError(f"Non-positive timings on stretch {s.id}")
        if s.source == "ExtraYogaAsanas":
            if not s.explicit_indian_name or not DEVANAGARI_RE.search(s.explicit_indian_name):
                raise AssertionError(f"ExtraYogaAsanas {s.id} missing Devanagari indianName")

    resolver = SanskritDevanagariResolver(phonetics_path)
    for s in catalog:
        s.resolved_indian_name = resolver.resolve_indian_name(s.name, s.explicit_indian_name)

    non_null_devanagari = sum(1 for s in catalog if s.resolved_indian_name)
    if non_null_devanagari != 101:
        raise AssertionError(f"Expected 101 resolved Devanagari names, got {non_null_devanagari}")

    # -------------------------------------------------------------------------
    # Tier 1b: Drawable Hygiene & Binary JPEG 1376x768 Validation
    # -------------------------------------------------------------------------
    for path in res_dir.rglob("*"):
        if not path.is_file():
            continue
        stem = path.stem
        if stem in LEGACY_15_STEMS:
            raise AssertionError(f"Legacy mixed-style drawable still present: {path}")
        if path.parent.name.startswith("drawable") and stem.startswith("img_stretch_") and path.suffix.lower() in (".png", ".webp"):
            raise AssertionError(f"Non-JPEG stretch drawable forbidden: {path}")

    for required_asset in (
        drawable_dir / "img_app_icon.jpg",
        drawable_dir / "ic_launcher_background.xml",
        drawable_dir / "ic_launcher_foreground.xml",
    ):
        if not required_asset.is_file():
            raise AssertionError(f"Required app launcher asset missing: {required_asset}")

    stretch_jpegs = sorted(drawable_dir.glob("img_stretch_*"))
    if not stretch_jpegs:
        raise AssertionError("No img_stretch_* drawables found in drawable/")
    for jpeg_path in stretch_jpegs:
        if not VALID_DRAWABLE_FILENAME_RE.match(jpeg_path.name):
            raise AssertionError(f"Invalid stretch drawable filename: {jpeg_path.name}")
        inspect_jpeg(jpeg_path)

    # -------------------------------------------------------------------------
    # Tier 2: App Mapping Integrity & Guardrails
    # -------------------------------------------------------------------------
    artwork_map = parse_artwork_mappings(root)
    for key, drawable_stem in artwork_map.items():
        target_jpg = drawable_dir / f"{drawable_stem}.jpg"
        inspect_jpeg(target_jpg)

    mapped_by_stretch_id: Dict[str, Optional[str]] = {}
    for s in catalog:
        by_name = artwork_map.get(normalize_key(s.name))
        by_id = artwork_map.get(normalize_key(s.id))
        if by_name != by_id:
            raise AssertionError(
                f"Name vs ID mapping mismatch for '{s.id}': name->'{by_name}' vs id->'{by_id}'"
            )
        mapped_by_stretch_id[s.id] = by_name

    # -------------------------------------------------------------------------
    # Tier 3: Symmetrical Left/Right Pair Consistency (All 32 Pairs)
    # -------------------------------------------------------------------------
    if len(EXPECTED_LR_PAIRS) != 32:
        raise AssertionError(f"Expected 32 L/R pairs, got {len(EXPECTED_LR_PAIRS)}")
    catalog_ids = {s.id for s in catalog}
    for right_id, left_id in EXPECTED_LR_PAIRS:
        if right_id not in catalog_ids or left_id not in catalog_ids:
            raise AssertionError(f"L/R pair missing from catalog: ({right_id}, {left_id})")
        if mapped_by_stretch_id[right_id] != mapped_by_stretch_id[left_id]:
            raise AssertionError(
                f"L/R pair mismatch: {right_id}->{mapped_by_stretch_id[right_id]} vs "
                f"{left_id}->{mapped_by_stretch_id[left_id]}"
            )

    mapped_count = sum(1 for v in mapped_by_stretch_id.values() if v is not None)
    unmapped_count = 150 - mapped_count

    # -------------------------------------------------------------------------
    # Tier 3/4 (Final Phase): HTML Visual Gallery Completeness
    # -------------------------------------------------------------------------
    if phase == "final":
        g_mapped, g_unmapped = verify_gallery_html(root, catalog, mapped_by_stretch_id)
        print(
            f"[PASS] verify_stretch_coach.py --phase final: "
            f"150 catalog items (101 Devanagari), {len(stretch_jpegs)} unified 1376x768 JPEGs, "
            f"0 legacy drawables, 32 L/R pairs consistent, "
            f"gallery verified ({g_mapped} mapped, {g_unmapped} unmapped)."
        )
    else:
        print(
            f"[PASS] verify_stretch_coach.py --phase m1: "
            f"150 catalog items (101 Devanagari), {len(stretch_jpegs)} unified 1376x768 JPEGs, "
            f"0 legacy drawables, 32 L/R pairs consistent, "
            f"app mappings verified ({mapped_count} mapped, {unmapped_count} unmapped; gallery skipped in m1)."
        )


def main() -> int:
    parser = argparse.ArgumentParser(description="Stretch Coach E2E Verification Suite")
    parser.add_argument(
        "--phase",
        choices=("m1", "final"),
        default="final",
        help="Verification phase: 'm1' skips stretch_gallery.html; 'final' verifies everything.",
    )
    parser.add_argument(
        "--root",
        type=pathlib.Path,
        default=pathlib.Path(__file__).resolve().parents[2],
        help="Repository root directory.",
    )
    args = parser.parse_args()

    try:
        run_verification(args.root.resolve(), args.phase)
    except AssertionError as exc:
        print(f"[FAIL] {exc}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
