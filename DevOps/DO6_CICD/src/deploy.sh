#!/bin/bash
set -euo pipefail

VM2_USER="runner"
VM2_IP="192.168.1.102"
TARGET_DIR="/usr/local/bin"

ARTIFACT_CAT="src/cat/s21_cat"
ARTIFACT_GREP="src/grep/s21_grep"



if [ ! -f "$ARTIFACT_CAT" ]; then
  echo "Error: $ARTIFACT_CAT not found"
  exit 1
fi

if [ ! -f "$ARTIFACT_GREP" ]; then
  echo "Error: $ARTIFACT_GREP not found"
  exit 1
fi

scp -o StrictHostKeyChecking=no "$ARTIFACT_CAT" "$VM2_USER@$VM2_IP:$TARGET_DIR"
scp -o StrictHostKeyChecking=no "$ARTIFACT_GREP" "$VM2_USER@$VM2_IP:$TARGET_DIR"

ssh -o StrictHostKeyChecking=no "$VM2_USER@$VM2_IP" "chmod +x $TARGET_DIR/s21_cat $TARGET_DIR/s21_grep"

echo "Deploy OK: $ARTIFACT_CAT, $ARTIFACT_GREP -> $VM2_USER@$VM2_IP:$TARGET_DIR"