# Fuel Density phone test APK

1. Extract this source ZIP and copy its contents into the root of the Fuel Density GitHub repository, where `settings.gradle.kts` and `app/` already live. Include the hidden `.github` directory. Commit the changes to the default branch.
2. Open **Actions → Build Fuel Density APK → Run workflow**.
3. In the successful run, open **Artifacts → Fuel-Density-Test**, extract the downloaded ZIP, and open `Fuel-Density-Test.apk` on the phone.

The APK requires Android 7.0 (API 24) or newer. Its application ID ends in `.phonetest` so it can install alongside the original app and uses a separate local database. The GitHub runner creates its own test signing key. For a later build signed by a different runner key, back up any data from the test copy before uninstalling that copy. The original app has a separate application ID and does not need to be removed.

If the build fails, open the red step in the Actions log and capture the first `What went wrong` block. If installation fails, run `adb install -r Fuel-Density-Test.apk` to read Android's exact error.
