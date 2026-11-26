#!/usr/bin/env bash
set -euo pipefail

# One-command Stretch Coach publisher for GitHub Releases + GitHub Pages (latest.json & index.html).
# Usage: ./scripts/publish_release.sh <versionName> <versionCode> "<releaseNotes>"
# Example: ./scripts/publish_release.sh 1.1.0 2 "Added new evening cooldown routine"

VERSION_NAME="${1:?Usage: $0 <versionName> <versionCode> \"<releaseNotes>\"}"
VERSION_CODE="${2:?Usage: $0 <versionName> <versionCode> \"<releaseNotes>\"}"
RELEASE_NOTES="${3:-Bug fixes and performance improvements.}"
RELEASES_REPO="hgarg5072/StretchCoach-Releases"
TAG="v${VERSION_NAME}"
APK_PATH="app/build/outputs/apk/release/app-release.apk"

export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home}"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:/opt/homebrew/bin:$PATH"

if [ ! -f "$APK_PATH" ]; then
  echo "Building signed Release APK..."
  ./gradlew assembleRelease --no-daemon
fi

SHA256=$(shasum -a 256 "$APK_PATH" | awk '{print $1}')
SIZE_BYTES=$(stat -f%z "$APK_PATH")
SIZE_MB=$(python3 -c "print(f'{$SIZE_BYTES / (1024*1024):.1f}')")
APK_URL="https://github.com/${RELEASES_REPO}/releases/download/${TAG}/StretchCoach.apk"

echo "Publishing ${TAG} (${SIZE_MB} MB, SHA-256: ${SHA256})..."

# Copy with clean public filename
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT
cp "$APK_PATH" "$TMP_DIR/StretchCoach.apk"

if gh release view "$TAG" --repo "$RELEASES_REPO" >/dev/null 2>&1; then
  gh release upload "$TAG" "$TMP_DIR/StretchCoach.apk" --repo "$RELEASES_REPO" --clobber
  gh release edit "$TAG" --repo "$RELEASES_REPO" --notes "${RELEASE_NOTES}

**SHA-256:** \`${SHA256}\`"
else
  gh release create "$TAG" "$TMP_DIR/StretchCoach.apk" \
    --repo "$RELEASES_REPO" \
    --title "Stretch Coach ${TAG}" \
    --notes "${RELEASE_NOTES}

**SHA-256:** \`${SHA256}\`"
fi

# Update latest.json and index.html in StretchCoach-Releases (GitHub Pages)
git clone --depth 1 "https://github.com/${RELEASES_REPO}.git" "$TMP_DIR/repo"
python3 - <<EOF
import json, pathlib, re

repo_dir = pathlib.Path("$TMP_DIR/repo")
latest = {
    "versionCode": int("$VERSION_CODE"),
    "versionName": "$VERSION_NAME",
    "apkUrl": "$APK_URL",
    "sha256": "$SHA256",
    "sizeBytes": int("$SIZE_BYTES"),
    "releaseNotes": """$RELEASE_NOTES"""
}
(repo_dir / "latest.json").write_text(json.dumps(latest, indent=2) + "\n")

idx = repo_dir / "index.html"
if idx.exists():
    import html as html_lib
    html = idx.read_text()
    html = re.sub(r'<!--VERSION-->.*?<!--/VERSION-->', f'<!--VERSION-->v$VERSION_NAME<!--/VERSION-->', html, flags=re.S)
    html = re.sub(r'<!--SIZE-->.*?<!--/SIZE-->', f'<!--SIZE-->$SIZE_MB MB<!--/SIZE-->', html, flags=re.S)
    html = re.sub(r'<!--SHA256-->.*?<!--/SHA256-->', f'<!--SHA256-->$SHA256<!--/SHA256-->', html, flags=re.S)
    escaped_notes = html_lib.escape("""$RELEASE_NOTES""".strip())
    html = re.sub(r'<!--NOTES-->.*?<!--/NOTES-->', f'<!--NOTES-->{escaped_notes}<!--/NOTES-->', html, flags=re.S)
    html = re.sub(r'href="https://github\.com/hgarg5072/StretchCoach-Releases/releases/download/[^"]+"', f'href="$APK_URL"', html)
    idx.write_text(html)
EOF

(
  cd "$TMP_DIR/repo"
  git add latest.json index.html
  git commit -m "Release Stretch Coach ${TAG} (SHA-256 verified)"
  git push origin main
)

echo "Published ${TAG} successfully!"
echo "Landing page: https://hgarg5072.github.io/StretchCoach-Releases/"
echo "Metadata JSON: https://hgarg5072.github.io/StretchCoach-Releases/latest.json"
echo "Direct APK: ${APK_URL}"
