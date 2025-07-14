# Maven Central Migration Guide

## Overview
OSSRH was shut down on June 30, 2025, and all publishing has been migrated to the new Maven Central Portal.

## Changes Made

### 1. Updated gradle.properties
- Changed `SONATYPE_HOST=S01` to `SONATYPE_HOST=CENTRAL_PORTAL`
- This tells the publishing plugin to use the new Central Portal instead of OSSRH

### 2. Updated Publishing Plugin
- Updated `com.vanniktech.maven.publish` from `0.31.0` to `0.32.0`
- Newer versions have better support for the Central Portal

### 3. Fixed Deprecated Warnings
- Removed `kotlin.js.compiler=ir` as it's deprecated and has no effect

## Required Actions

### 1. Update Your Secrets
You need to update your GitHub secrets to use **Portal User Tokens** instead of OSSRH tokens:

1. Go to [Maven Central Portal](https://central.sonatype.com/)
2. Log in with your OSSRH credentials
3. Generate a new **Portal User Token**
4. Update your GitHub repository secrets:
   - `ORG_GRADLE_PROJECT_MAVENCENTRALUSERNAME`: Use your Portal username
   - `ORG_GRADLE_PROJECT_MAVENCENTRALPASSWORD`: Use your Portal user token (not your old OSSRH password)

### 2. Verify Your Namespace
- Log into the Portal and verify all your namespaces are migrated
- If any are missing, contact Central Support

### 3. Test Publishing
- Try publishing a test version to make sure everything works
- The process should be the same, but now targets the new Portal

## Alternative: OSSRH Staging API Service
If you prefer to use the compatibility service that translates old OSSRH API calls:

Add this to your `gradle.properties`:
```properties
# Use compatibility service (optional)
SONATYPE_HOST=DEFAULT
# This will use the compatibility service at https://oss.sonatype.org/
```

However, using `CENTRAL_PORTAL` is recommended as it's the direct approach.

## Troubleshooting

### If you get authentication errors:
1. Make sure you're using Portal User Tokens, not OSSRH passwords
2. Verify your tokens are correctly set in GitHub secrets
3. Check that your namespace is properly migrated in the Portal

### If you still see 403 errors:
1. Your tokens might be incorrect
2. Your namespace might not be fully migrated
3. Contact Central Support for assistance

## Resources
- [Maven Central Portal](https://central.sonatype.com/)
- [Migration Documentation](https://central.sonatype.org/publish/publish-migrate/)
- [Central Support](https://central.sonatype.org/support/)
