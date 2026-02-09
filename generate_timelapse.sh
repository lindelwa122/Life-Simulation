#!/usr/bin/env bash

OUTDIR=${1:-output}
FPS=${2:-10}
OUTFILE=${3:-timelapse.mp4}

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo "ffmpeg not found, skipping timelapse generation"
  exit 0
fi

if [ ! -d "$OUTDIR" ]; then
  echo "Output directory $OUTDIR does not exist"
  exit 1
fi

# Check for files
shopt -s nullglob
files=("$OUTDIR"/gen-*.png)
shopt -u nullglob
if [ ${#files[@]} -eq 0 ]; then
  echo "No snapshot files found in $OUTDIR"
  exit 1
fi

# Generate timelapse using seq format (expects gen-00000.png ...)
ffmpeg -y -framerate "$FPS" -i "$OUTDIR/*" -c:v libx264 -pix_fmt yuv420p "$OUTDIR/$OUTFILE"

if [ $? -eq 0 ]; then
  echo "Timelapse created: $OUTDIR/$OUTFILE"
else
  echo "ffmpeg failed"
fi
