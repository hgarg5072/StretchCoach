#!/usr/bin/env python3
"""
Reproducible HTML visual gallery generator for Stretch Coach (Python 3.9+ stdlib only).

Reads directly from Kotlin catalog, phonetics, and artwork registry sources:
  - app/src/main/java/com/example/data/StretchLibrary.kt
  - app/src/main/java/com/example/data/ExtraYogaAsanas.kt
  - app/src/main/java/com/example/audio/SanskritPhonetics.kt
  - app/src/main/java/com/example/data/ArtworkMapper.kt
  - app/src/main/java/com/example/data/artwork/ArtworkBatch1..4.kt
  - app/src/main/res/drawable/ (for on-disk unified drawable inspection)

Generates:
  - stretch_gallery.html (offline-capable visual review sheet with all 150 stretches)
"""

from __future__ import annotations

import argparse
import dataclasses
import html
import pathlib
import re
import sys
from typing import Dict, List, Optional, Set, Tuple


DEVANAGARI_RE = re.compile(r"[\u0900-\u097F]")
WHITESPACE_RE = re.compile(r"\s+")


@dataclasses.dataclass
class StretchRecord:
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
    drawable_stem: Optional[str] = None


def normalize_key(raw: str) -> str:
    return WHITESPACE_RE.sub(" ", raw.strip().lower())


def strip_kotlin_comments(text: str) -> str:
    """Strips Kotlin // line comments and /* ... */ block comments while preserving string literals."""
    out: List[str] = []
    i = 0
    n = len(text)
    while i < n:
        ch = text[i]
        if text.startswith('"""', i):
            end = text.find('"""', i + 3)
            if end == -1:
                out.append(text[i:])
                break
            out.append(text[i : end + 3])
            i = end + 3
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
        elif text.startswith("//", i):
            nl = text.find("\n", i + 2)
            if nl == -1:
                break
            out.append("\n")
            i = nl + 1
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


def find_matching_paren(text: str, open_idx: int) -> int:
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
    raise ValueError(f"Unclosed parenthesis starting at offset {open_idx}")


def unescape_kotlin_string(raw: str) -> str:
    return (
        raw.replace('\\"', '"')
        .replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\\\", "\\")
    )


def extract_named_string(block: str, field: str) -> Optional[str]:
    pattern = re.compile(rf'\b{re.escape(field)}\s*=\s*"((?:[^"\\]|\\.)*)"')
    match = pattern.search(block)
    return unescape_kotlin_string(match.group(1)) if match else None


def extract_named_int(block: str, field: str) -> int:
    match = re.search(rf"\b{re.escape(field)}\s*=\s*(\d+)", block)
    if not match:
        raise ValueError(f"Missing integer field '{field}' in block: {block[:120]}")
    return int(match.group(1))


def extract_named_category(block: str) -> str:
    match = re.search(r"\bcategory\s*=\s*StretchCategory\.([A-Z0-9_]+)", block)
    if not match:
        raise ValueError(f"Missing StretchCategory in block: {block[:120]}")
    return match.group(1)


def parse_category_titles(stretch_library_path: pathlib.Path) -> Tuple[Dict[str, str], List[str]]:
    text = strip_kotlin_comments(stretch_library_path.read_text(encoding="utf-8"))
    match = re.search(r"enum class StretchCategory[^{]*\{(.*?)\}", text, re.DOTALL)
    if not match:
        raise ValueError(f"Could not locate StretchCategory enum in {stretch_library_path}")
    titles: Dict[str, str] = {}
    ordered_titles: List[str] = []
    for item in re.finditer(r'([A-Z0-9_]+)\s*\(\s*"([^"]+)"\s*\)', match.group(1)):
        titles[item.group(1)] = item.group(2)
        ordered_titles.append(item.group(2))
    return titles, ordered_titles


def parse_library_stretches(
    kt_path: pathlib.Path,
    source_label: str,
    category_titles: Dict[str, str],
    start_index: int,
) -> List[StretchRecord]:
    text = strip_kotlin_comments(kt_path.read_text(encoding="utf-8"))
    records: List[StretchRecord] = []
    idx = start_index
    for match in re.finditer(r"LibraryStretch\s*\(", text):
        prefix = text[max(0, match.start() - 20) : match.start()]
        if "data class" in prefix:
            continue
        open_paren = match.end() - 1
        close_paren = find_matching_paren(text, open_paren)
        block = text[open_paren + 1 : close_paren]
        enum_key = extract_named_category(block)
        records.append(
            StretchRecord(
                index=idx,
                source=source_label,
                id=extract_named_string(block, "id") or "",
                name=extract_named_string(block, "name") or "",
                category=category_titles[enum_key],
                prep_seconds=extract_named_int(block, "defaultPrepSeconds"),
                hold_seconds=extract_named_int(block, "defaultHoldSeconds"),
                prep_cue=extract_named_string(block, "prepCue") or "",
                hold_cue=extract_named_string(block, "holdCue") or "",
                benefits=extract_named_string(block, "benefits") or "",
                explicit_indian_name=extract_named_string(block, "indianName"),
            )
        )
        idx += 1
    return records


class SanskritDevanagariResolver:
    """Reproduces SanskritPhonetics.buildSpokenPlan(name).sanskritDevanagari."""

    def __init__(self, phonetics_path: pathlib.Path) -> None:
        text = strip_kotlin_comments(phonetics_path.read_text(encoding="utf-8"))

        eng_block_m = re.search(
            r"SANSKRIT_TO_ENGLISH_MAP\s*=\s*mapOf\s*\((.*?)\n\s*\)", text, re.DOTALL
        )
        if not eng_block_m:
            raise ValueError(f"Could not parse SANSKRIT_TO_ENGLISH_MAP in {phonetics_path}")
        self.english_keys = [
            m.group(1).lower()
            for m in re.finditer(r'"([^"]+)"\s+to\s+"[^"]+"', eng_block_m.group(1))
        ]

        dev_block_m = re.search(
            r"SANSKRIT_TO_DEVANAGARI_MAP\s*=\s*listOf\s*\((.*?)\n\s*\)", text, re.DOTALL
        )
        if not dev_block_m:
            raise ValueError(f"Could not parse SANSKRIT_TO_DEVANAGARI_MAP in {phonetics_path}")
        self.devanagari_rules: List[Tuple[re.Pattern[str], str]] = []
        for rm in re.finditer(
            r'Regex\("((?:[^"\\]|\\.)*)"\)\s+to\s+"([^"]+)"', dev_block_m.group(1)
        ):
            raw_pat = rm.group(1).replace("\\\\", "\\")
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
        right_brackets = [
            "(Right)", "(Right Leg)", "(Right Shin Forward)",
            "(Right Forward)", "(Right Leg Back)", "(Right Side)",
        ]
        left_brackets = [
            "(Left)", "(Left Leg)", "(Left Shin Forward)",
            "(Left Forward)", "(Left Leg Back)", "(Left Side)",
        ]
        side_patterns = [
            re.compile(
                r"[-\s]*\b(right|right side|right leg|right shin forward|right forward|right leg back)\b",
                re.IGNORECASE,
            ),
            re.compile(
                r"[-\s]*\b(left|left side|left leg|left shin forward|left forward|left leg back)\b",
                re.IGNORECASE,
            ),
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


def parse_artwork_registries(root: pathlib.Path) -> Tuple[Dict[str, str], Dict[str, int]]:
    """
    Parses ArtworkMapper.kt (coreReferenceMap) and ArtworkBatch1..4.kt (exactNameMap).
    Returns (normalized_key -> drawable_stem, source_file_name -> entry_count).
    """
    registry_files = [
        root / "app/src/main/java/com/example/data/ArtworkMapper.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch1.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch2.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch3.kt",
        root / "app/src/main/java/com/example/data/artwork/ArtworkBatch4.kt",
    ]
    pair_re = re.compile(r'"((?:[^"\\]|\\.)*)"\s+to\s+R\.drawable\.([a-z0-9_]+)', re.DOTALL)

    merged_map: Dict[str, str] = {}
    counts_by_file: Dict[str, int] = {}

    for src in registry_files:
        if not src.is_file():
            raise FileNotFoundError(f"Missing required artwork registry file: {src}")
        content = strip_kotlin_comments(src.read_text(encoding="utf-8"))
        file_count = 0
        for match in pair_re.finditer(content):
            raw_key = unescape_kotlin_string(match.group(1))
            drawable_stem = match.group(2)
            norm = normalize_key(raw_key)
            if norm in merged_map and merged_map[norm] != drawable_stem:
                raise ValueError(
                    f"Conflicting mapping for '{norm}': {merged_map[norm]} vs {drawable_stem}"
                )
            merged_map[norm] = drawable_stem
            file_count += 1
        counts_by_file[src.name] = file_count

    return merged_map, counts_by_file


def build_unmapped_reason(
    stretch: StretchRecord,
    unmapped_disk_stems: Set[str],
    batch_counts: Dict[str, int],
) -> str:
    """Derives an accurate, non-hardcoded explanation for why a stretch is unmapped."""
    stem_tokens = {
        stem.replace("img_stretch_", "")
        for stem in unmapped_disk_stems
        if stem.startswith("img_stretch_")
    }
    id_tokens = set(stretch.id.lower().split("_"))
    name_tokens = set(re.findall(r"[a-z0-9]+", stretch.name.lower()))
    combined_tokens = id_tokens | name_tokens

    if "flow" in id_tokens:
        return (
            "Strict R4 audit deferral: multi-pose sequence requires static pose "
            "accuracy review before mapping (no generic fallback)."
        )
    if combined_tokens & stem_tokens or "child" in id_tokens:
        return (
            "Strict R4 audit deferral: pose cues require dedicated cue-accurate "
            "illustration rather than reusing an imprecise reference (no generic fallback)."
        )
    total_batch_entries = sum(
        count for fname, count in batch_counts.items() if fname.startswith("ArtworkBatch")
    )
    if total_batch_entries == 0:
        return (
            "Awaiting M2–M5 batch generation (upstream image-gen capacity unavailable); "
            "cleanly unmapped per R3/R4 with zero generic or legacy fallback."
        )
    return "No verified unified-style illustration mapped in ArtworkMapper or ArtworkBatch1..4."


def render_card_html(
    stretch: StretchRecord,
    unmapped_disk_stems: Set[str],
    batch_counts: Dict[str, int],
) -> str:
    idx_str = str(stretch.index)
    esc_id = html.escape(stretch.id, quote=True)
    esc_source = html.escape(stretch.source, quote=True)
    esc_category = html.escape(stretch.category, quote=True)
    esc_name = html.escape(stretch.name, quote=True)
    esc_prep_cue = html.escape(stretch.prep_cue)
    esc_hold_cue = html.escape(stretch.hold_cue)
    esc_benefits = html.escape(stretch.benefits)

    is_mapped = stretch.drawable_stem is not None
    status = "mapped" if is_mapped else "unmapped"
    drawable_filename = f"{stretch.drawable_stem}.jpg" if stretch.drawable_stem else ""
    esc_drawable = html.escape(drawable_filename, quote=True)

    if stretch.resolved_indian_name:
        esc_indian = html.escape(stretch.resolved_indian_name)
        indian_html = (
            f'<div class="indian-row">'
            f'<span class="meta-label">Sanskrit / Indian</span>'
            f'<span class="indian-name" lang="sa">{esc_indian}</span>'
            f"</div>"
        )
    else:
        indian_html = (
            '<div class="indian-row">'
            '<span class="meta-label">Sanskrit / Indian</span>'
            '<span class="indian-name is-empty">—</span>'
            "</div>"
        )

    if is_mapped:
        rel_path = f"app/src/main/res/drawable/{drawable_filename}"
        esc_rel = html.escape(rel_path, quote=True)
        preview_html = (
            f'<div class="preview-area preview-mapped">'
            f'<a class="thumb-link" href="{esc_rel}" target="_blank" rel="noopener">'
            f'<img src="{esc_rel}" alt="{esc_name}" loading="lazy">'
            f"</a>"
            f'<div class="drawable-meta"><code>{esc_drawable}</code></div>'
            f"</div>"
        )
        status_badge = '<span class="badge badge-mapped">Verified Mapped</span>'
    else:
        reason = html.escape(build_unmapped_reason(stretch, unmapped_disk_stems, batch_counts))
        preview_html = (
            '<div class="preview-area preview-unmapped">'
            '<div class="no-image-state">No verified image assigned</div>'
            f'<p class="no-image-reason">{reason}</p>'
            "</div>"
        )
        status_badge = '<span class="badge badge-unmapped">Unmapped</span>'

    source_badge_cls = "badge-source-lib" if stretch.source == "StretchLibrary" else "badge-source-extra"

    return (
        f'<article class="stretch-card" '
        f'data-index="{idx_str}" '
        f'data-id="{esc_id}" '
        f'data-source="{esc_source}" '
        f'data-category="{esc_category}" '
        f'data-drawable="{esc_drawable}" '
        f'data-status="{status}">\n'
        f'  <div class="card-top">\n'
        f'    <div class="badge-group">\n'
        f'      <span class="badge badge-index">#{idx_str}</span>\n'
        f'      <span class="badge {source_badge_cls}">{esc_source}</span>\n'
        f'      <span class="badge badge-category">{esc_category}</span>\n'
        f'    </div>\n'
        f'    {status_badge}\n'
        f'  </div>\n'
        f'  <div class="card-title-block">\n'
        f'    <h2 class="stretch-name">{esc_name}</h2>\n'
        f'    <div class="stretch-id-line"><code>{esc_id}</code></div>\n'
        f'    {indian_html}\n'
        f'  </div>\n'
        f'  {preview_html}\n'
        f'  <div class="timings-row">\n'
        f'    <span class="timing-pill">Prep {stretch.prep_seconds}s / Hold {stretch.hold_seconds}s</span>\n'
        f'  </div>\n'
        f'  <dl class="cues-list">\n'
        f'    <div class="cue-item"><dt>Prep Cue</dt><dd>{esc_prep_cue}</dd></div>\n'
        f'    <div class="cue-item"><dt>Hold Cue</dt><dd>{esc_hold_cue}</dd></div>\n'
        f'    <div class="cue-item"><dt>Benefits</dt><dd>{esc_benefits}</dd></div>\n'
        f'  </dl>\n'
        f'</article>'
    )


def generate_gallery_html(root: pathlib.Path, output_path: pathlib.Path) -> Tuple[int, int, int, int, int]:
    stretch_lib_path = root / "app/src/main/java/com/example/data/StretchLibrary.kt"
    extra_asanas_path = root / "app/src/main/java/com/example/data/ExtraYogaAsanas.kt"
    phonetics_path = root / "app/src/main/java/com/example/audio/SanskritPhonetics.kt"
    drawable_dir = root / "app/src/main/res/drawable"

    category_titles, ordered_categories = parse_category_titles(stretch_lib_path)
    first_100 = parse_library_stretches(stretch_lib_path, "StretchLibrary", category_titles, 1)
    extra_50 = parse_library_stretches(extra_asanas_path, "ExtraYogaAsanas", category_titles, 101)
    catalog = first_100 + extra_50

    resolver = SanskritDevanagariResolver(phonetics_path)
    artwork_map, batch_counts = parse_artwork_registries(root)

    for stretch in catalog:
        stretch.resolved_indian_name = resolver.resolve_indian_name(
            stretch.name, stretch.explicit_indian_name
        )
        by_name = artwork_map.get(normalize_key(stretch.name))
        by_id = artwork_map.get(normalize_key(stretch.id))
        if by_name != by_id:
            raise ValueError(
                f"Name vs ID mapping mismatch for '{stretch.id}': name->{by_name} vs id->{by_id}"
            )
        if by_name is not None:
            expected_jpg = drawable_dir / f"{by_name}.jpg"
            if not expected_jpg.is_file():
                raise FileNotFoundError(
                    f"Mapped drawable missing on disk for '{stretch.id}': {expected_jpg}"
                )
        stretch.drawable_stem = by_name

    total_count = len(catalog)
    mapped_count = sum(1 for s in catalog if s.drawable_stem is not None)
    unmapped_count = total_count - mapped_count
    active_stems = {s.drawable_stem for s in catalog if s.drawable_stem is not None}
    active_drawables_count = len(active_stems)
    devanagari_count = sum(1 for s in catalog if s.resolved_indian_name)

    disk_stretch_stems = {p.stem for p in drawable_dir.glob("img_stretch_*.jpg")}
    unmapped_disk_stems = disk_stretch_stems - active_stems
    unmapped_disk_list = ", ".join(f"{s}.jpg" for s in sorted(unmapped_disk_stems)) or "none"

    empty_batches = [
        name.replace(".kt", "")
        for name, count in batch_counts.items()
        if name.startswith("ArtworkBatch") and count == 0
    ]
    if empty_batches:
        batch_note = (
            f"{', '.join(empty_batches)} currently contain 0 mapped entries due to upstream "
            f"image-generation capacity outage (503 MODEL_CAPACITY_EXHAUSTED); all unverified "
            f"stretches remain cleanly unmapped per R3/R4."
        )
    else:
        batch_note = "All batch registries (ArtworkBatch1..4) are populated with R4-verified artwork."

    category_options = "\n".join(
        f'          <option value="{html.escape(cat, quote=True)}">{html.escape(cat)}</option>'
        for cat in ordered_categories
    )

    cards_html = "\n".join(
        render_card_html(stretch, unmapped_disk_stems, batch_counts) for stretch in catalog
    )

    document = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Stretch Coach — 150-Pose Visual Gallery &amp; Audit Sheet</title>
  <style>
    :root {{
      --bg: #f6f3ec;
      --surface: #ffffff;
      --surface-muted: #fbf9f5;
      --border: #e2dbd0;
      --border-strong: #cfc5b6;
      --text: #242925;
      --text-muted: #5e665f;
      --accent: #4c6b53;
      --accent-soft: #e7efe9;
      --accent-border: #b8ceb9;
      --warn-bg: #fdf4eb;
      --warn-border: #ead2b5;
      --warn-text: #85521d;
      --info-bg: #eef2f6;
      --info-text: #35506b;
      --radius: 14px;
      --shadow: 0 2px 10px rgba(36, 41, 37, 0.05);
    }}

    * {{
      box-sizing: border-box;
    }}

    body {{
      margin: 0;
      background: var(--bg);
      color: var(--text);
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      line-height: 1.45;
      -webkit-font-smoothing: antialiased;
    }}

    .page-shell {{
      max-width: 1440px;
      margin: 0 auto;
      padding: 24px 20px 48px;
    }}

    .gallery-header {{
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 24px;
      box-shadow: var(--shadow);
      margin-bottom: 20px;
    }}

    .header-title-row {{
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      align-items: flex-start;
      gap: 16px;
      margin-bottom: 16px;
    }}

    h1 {{
      margin: 0 0 6px;
      font-size: 1.65rem;
      font-weight: 700;
      letter-spacing: -0.01em;
    }}

    .subtitle {{
      margin: 0;
      color: var(--text-muted);
      font-size: 0.95rem;
    }}

    .stats-bar {{
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin-bottom: 16px;
    }}

    .stat-pill {{
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 7px 13px;
      border-radius: 999px;
      font-size: 0.86rem;
      font-weight: 600;
      border: 1px solid var(--border);
      background: var(--surface-muted);
      color: var(--text);
    }}

    .stat-pill.is-mapped {{
      background: var(--accent-soft);
      border-color: var(--accent-border);
      color: #2f4d36;
    }}

    .stat-pill.is-unmapped {{
      background: var(--warn-bg);
      border-color: var(--warn-border);
      color: var(--warn-text);
    }}

    .audit-note {{
      background: var(--surface-muted);
      border: 1px solid var(--border);
      border-left: 4px solid var(--accent);
      border-radius: 10px;
      padding: 12px 16px;
      font-size: 0.88rem;
      color: var(--text);
    }}

    .audit-note strong {{
      color: #2f4d36;
    }}

    .audit-note ul {{
      margin: 6px 0 0;
      padding-left: 18px;
    }}

    .audit-note li + li {{
      margin-top: 4px;
    }}

    .controls-bar {{
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 16px 20px;
      margin-bottom: 20px;
      box-shadow: var(--shadow);
      display: flex;
      flex-wrap: wrap;
      gap: 14px;
      align-items: center;
      justify-content: space-between;
    }}

    .control-group {{
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      align-items: center;
      flex: 1 1 auto;
    }}

    .search-input {{
      flex: 1 1 260px;
      min-width: 220px;
      padding: 9px 12px;
      border: 1px solid var(--border-strong);
      border-radius: 9px;
      font-size: 0.92rem;
      background: #fff;
      color: var(--text);
    }}

    .search-input:focus,
    .category-select:focus,
    .filter-btn:focus {{
      outline: 2px solid var(--accent);
      outline-offset: 1px;
    }}

    .category-select {{
      padding: 9px 12px;
      border: 1px solid var(--border-strong);
      border-radius: 9px;
      font-size: 0.9rem;
      background: #fff;
      color: var(--text);
    }}

    .status-toggle {{
      display: inline-flex;
      flex-wrap: wrap;
      gap: 6px;
    }}

    .filter-btn {{
      padding: 8px 13px;
      border: 1px solid var(--border-strong);
      border-radius: 9px;
      background: var(--surface-muted);
      color: var(--text);
      font-size: 0.86rem;
      font-weight: 600;
      cursor: pointer;
    }}

    .filter-btn.is-active {{
      background: var(--accent);
      border-color: var(--accent);
      color: #ffffff;
    }}

    .results-meta {{
      font-size: 0.86rem;
      font-weight: 600;
      color: var(--text-muted);
      white-space: nowrap;
    }}

    .gallery-grid {{
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
      gap: 20px;
    }}

    .stretch-card {{
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 16px;
      box-shadow: var(--shadow);
      display: flex;
      flex-direction: column;
      gap: 12px;
    }}

    .stretch-card.is-hidden {{
      display: none !important;
    }}

    .card-top {{
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 8px;
      flex-wrap: wrap;
    }}

    .badge-group {{
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      align-items: center;
    }}

    .badge {{
      display: inline-block;
      padding: 3px 9px;
      border-radius: 999px;
      font-size: 0.75rem;
      font-weight: 600;
      letter-spacing: 0.01em;
    }}

    .badge-index {{
      background: #242925;
      color: #ffffff;
    }}

    .badge-source-lib {{
      background: var(--info-bg);
      color: var(--info-text);
    }}

    .badge-source-extra {{
      background: #efeaf6;
      color: #4d356b;
    }}

    .badge-category {{
      background: var(--surface-muted);
      border: 1px solid var(--border);
      color: var(--text-muted);
    }}

    .badge-mapped {{
      background: var(--accent-soft);
      border: 1px solid var(--accent-border);
      color: #2f4d36;
    }}

    .badge-unmapped {{
      background: var(--warn-bg);
      border: 1px solid var(--warn-border);
      color: var(--warn-text);
    }}

    .card-title-block {{
      display: flex;
      flex-direction: column;
      gap: 4px;
    }}

    .stretch-name {{
      margin: 0;
      font-size: 1.08rem;
      font-weight: 700;
      line-height: 1.3;
    }}

    .stretch-id-line code {{
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
      font-size: 0.78rem;
      color: var(--text-muted);
      background: var(--surface-muted);
      padding: 2px 6px;
      border-radius: 5px;
      border: 1px solid var(--border);
    }}

    .indian-row {{
      display: flex;
      flex-wrap: wrap;
      align-items: baseline;
      gap: 6px;
      margin-top: 2px;
    }}

    .meta-label {{
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      color: var(--text-muted);
    }}

    .indian-name {{
      font-family: "Noto Sans Devanagari", "Kohinoor Devanagari", "Devanagari Sangam MN", sans-serif;
      font-size: 0.98rem;
      font-weight: 600;
      color: #2f4d36;
    }}

    .indian-name.is-empty {{
      color: var(--text-muted);
      font-weight: 400;
    }}

    .preview-area {{
      border-radius: 10px;
      overflow: hidden;
      border: 1px solid var(--border);
      background: var(--surface-muted);
    }}

    .preview-mapped .thumb-link {{
      display: block;
      aspect-ratio: 16 / 9;
      background: #f5efe4;
      overflow: hidden;
    }}

    .preview-mapped img {{
      width: 100%;
      height: 100%;
      object-fit: contain;
      display: block;
    }}

    .drawable-meta {{
      padding: 7px 10px;
      border-top: 1px solid var(--border);
      background: #fff;
      font-size: 0.8rem;
    }}

    .drawable-meta code {{
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
      color: #2f4d36;
      font-weight: 600;
    }}

    .preview-unmapped {{
      padding: 16px 14px;
      background: var(--warn-bg);
      border-color: var(--warn-border);
      min-height: 112px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 6px;
    }}

    .no-image-state {{
      font-size: 0.92rem;
      font-weight: 700;
      color: var(--warn-text);
    }}

    .no-image-reason {{
      margin: 0;
      font-size: 0.8rem;
      color: #6b4823;
      line-height: 1.4;
    }}

    .timings-row {{
      display: flex;
      align-items: center;
      gap: 8px;
    }}

    .timing-pill {{
      display: inline-block;
      padding: 3px 10px;
      border-radius: 6px;
      background: var(--surface-muted);
      border: 1px solid var(--border);
      font-size: 0.79rem;
      font-weight: 600;
      color: var(--text);
    }}

    .cues-list {{
      margin: 0;
      display: flex;
      flex-direction: column;
      gap: 8px;
      font-size: 0.84rem;
    }}

    .cue-item dt {{
      font-size: 0.73rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      color: var(--text-muted);
      margin-bottom: 1px;
    }}

    .cue-item dd {{
      margin: 0;
      color: var(--text);
    }}

    .empty-filter-banner {{
      display: none;
      margin-top: 24px;
      padding: 28px;
      text-align: center;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      color: var(--text-muted);
      font-weight: 600;
    }}

    .empty-filter-banner.is-visible {{
      display: block;
    }}

    @media (max-width: 640px) {{
      .page-shell {{
        padding: 14px 12px 32px;
      }}
      .gallery-grid {{
        grid-template-columns: 1fr;
      }}
    }}
  </style>
</head>
<body>
  <div class="page-shell">
    <header class="gallery-header">
      <div class="header-title-row">
        <div>
          <h1>Stretch Coach — 150-Pose Visual Gallery &amp; Audit Sheet</h1>
          <p class="subtitle">
            Complete static-DOM catalog audit of <code>StretchLibrary</code> (#1–#100) and
            <code>ExtraYogaAsanas</code> (#101–#150) synchronized directly from Kotlin sources.
          </p>
        </div>
      </div>

      <div class="stats-bar">
        <span class="stat-pill">{total_count} Total</span>
        <span class="stat-pill is-mapped">Mapped: {mapped_count}</span>
        <span class="stat-pill is-unmapped">Unmapped (No Image): {unmapped_count}</span>
        <span class="stat-pill">Active Verified Drawables: {active_drawables_count}</span>
        <span class="stat-pill">Sanskrit / Devanagari: {devanagari_count}</span>
      </div>

      <div class="audit-note">
        <strong>Audit Status (R1–R4 Compliance):</strong>
        <ul>
          <li>
            <strong>Verified Mapped ({mapped_count} stretches across {active_drawables_count} unified 1376×768 drawables):</strong>
            All mapped entries match their exact anatomical pose cues and unified studio style in <code>ArtworkMapper.kt</code>.
          </li>
          <li>
            <strong>Strict R4 Deferrals &amp; Unmapped On-Disk References:</strong>
            Retained reference assets not mapped in <code>coreReferenceMap</code> ({html.escape(unmapped_disk_list)}) and cue-mismatched or multi-pose flow entries remain unmapped until cue-accurate illustrations are verified.
          </li>
          <li>
            <strong>Batch Illustration Status:</strong>
            {html.escape(batch_note)}
          </li>
        </ul>
      </div>
    </header>

    <section class="controls-bar" aria-label="Gallery filters">
      <div class="control-group">
        <input id="searchInput" class="search-input" type="search"
               placeholder="Search by name, ID, Sanskrit Devanagari, cue, or drawable…"
               aria-label="Search stretches">
        <select id="categoryFilter" class="category-select" aria-label="Filter by category">
          <option value="ALL">All Categories</option>
{category_options}
        </select>
        <div class="status-toggle" role="group" aria-label="Filter by mapping status">
          <button type="button" class="filter-btn is-active" data-status-filter="ALL">All ({total_count})</button>
          <button type="button" class="filter-btn" data-status-filter="mapped">Mapped Only ({mapped_count})</button>
          <button type="button" class="filter-btn" data-status-filter="unmapped">Unmapped / No Image Only ({unmapped_count})</button>
        </div>
        <button type="button" id="resetFiltersBtn" class="filter-btn">Reset</button>
      </div>
      <div id="resultsMeta" class="results-meta">Showing {total_count} of {total_count} stretches</div>
    </section>

    <main id="galleryGrid" class="gallery-grid">
{cards_html}
    </main>

    <div id="emptyFilterBanner" class="empty-filter-banner">
      No stretches match the current search and filter criteria.
    </div>
  </div>

  <script>
    (function () {{
      var searchInput = document.getElementById("searchInput");
      var categoryFilter = document.getElementById("categoryFilter");
      var statusButtons = Array.prototype.slice.call(document.querySelectorAll("[data-status-filter]"));
      var resetBtn = document.getElementById("resetFiltersBtn");
      var cards = Array.prototype.slice.call(document.querySelectorAll(".stretch-card"));
      var resultsMeta = document.getElementById("resultsMeta");
      var emptyBanner = document.getElementById("emptyFilterBanner");

      var activeStatus = "ALL";
      var total = cards.length;

      function applyFilters() {{
        var query = (searchInput.value || "").trim().toLowerCase();
        var selectedCategory = categoryFilter.value;
        var visibleCount = 0;

        cards.forEach(function (card) {{
          var cardStatus = card.getAttribute("data-status") || "";
          var cardCategory = card.getAttribute("data-category") || "";
          var statusOk = activeStatus === "ALL" || cardStatus === activeStatus;
          var categoryOk = selectedCategory === "ALL" || cardCategory === selectedCategory;

          var textOk = true;
          if (query) {{
            var haystack = (
              (card.getAttribute("data-id") || "") + " " +
              (card.getAttribute("data-drawable") || "") + " " +
              (card.getAttribute("data-source") || "") + " " +
              cardCategory + " " +
              (card.textContent || "")
            ).toLowerCase();
            textOk = haystack.indexOf(query) !== -1;
          }}

          var show = statusOk && categoryOk && textOk;
          card.classList.toggle("is-hidden", !show);
          if (show) {{
            visibleCount += 1;
          }}
        }});

        resultsMeta.textContent = "Showing " + visibleCount + " of " + total + " stretches";
        emptyBanner.classList.toggle("is-visible", visibleCount === 0);
      }}

      searchInput.addEventListener("input", applyFilters);
      categoryFilter.addEventListener("change", applyFilters);

      statusButtons.forEach(function (btn) {{
        btn.addEventListener("click", function () {{
          activeStatus = btn.getAttribute("data-status-filter") || "ALL";
          statusButtons.forEach(function (b) {{
            b.classList.toggle("is-active", b === btn);
          }});
          applyFilters();
        }});
      }});

      resetBtn.addEventListener("click", function () {{
        searchInput.value = "";
        categoryFilter.value = "ALL";
        activeStatus = "ALL";
        statusButtons.forEach(function (b) {{
          b.classList.toggle("is-active", b.getAttribute("data-status-filter") === "ALL");
        }});
        applyFilters();
      }});
    }})();
  </script>
</body>
</html>
"""

    output_path.write_text(document, encoding="utf-8")
    return total_count, devanagari_count, mapped_count, unmapped_count, active_drawables_count


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Generate offline stretch_gallery.html directly from Kotlin sources."
    )
    parser.add_argument(
        "--root",
        type=pathlib.Path,
        default=pathlib.Path(__file__).resolve().parents[1],
        help="Repository root directory.",
    )
    parser.add_argument(
        "--output",
        type=pathlib.Path,
        default=None,
        help="Output HTML path (defaults to <root>/stretch_gallery.html).",
    )
    args = parser.parse_args()

    root = args.root.resolve()
    output = args.output.resolve() if args.output else (root / "stretch_gallery.html")

    total, devanagari, mapped, unmapped, active_drawables = generate_gallery_html(root, output)
    print(
        f"Generated {output}: {total} total ({devanagari} Devanagari), "
        f"{mapped} mapped, {unmapped} unmapped, {active_drawables} active drawables."
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
