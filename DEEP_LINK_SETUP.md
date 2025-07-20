# Deep Link Setup for Password Reset

This document explains how the deep linking functionality works for password reset in the Attendance Manager Android app.

## Overview

When users request a password reset, they receive an email with a link. Clicking this link opens the Android app directly to the ResetNewPasswordScreen, rather than opening a web browser.

## How It Works

### 1. Email Link Generation (Backend)
- Located in: `attendance_backend/resetPassword.js`
- The reset link is generated using the format: `http://Attendance_Manager/app1/reset-password?email={user_email}`
- This link is sent via email to the user

### 2. Android Manifest Configuration
- Located in: `app/src/main/AndroidManifest.xml`
- The app is registered to handle URLs with the scheme `http://Attendance_Manager`
- Intent filter configuration:
  ```xml
  <intent-filter android:autoVerify="true">
      <action android:name="android.intent.action.VIEW" />
      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />
      <data
          android:scheme="http"
          android:host="Attendance_Manager"
          android:pathPrefix="/app1/reset-password" />
  </intent-filter>
  ```

### 3. MainActivity Deep Link Handling
- Located in: `app/src/main/java/com/projects/attendancemanager/MainActivity.kt`
- The `handleDeepLink()` method processes incoming intents
- Extracts the email parameter from the URL
- Passes the email to the navigation system

### 4. Navigation Setup
- Located in: `app/src/main/java/com/projects/attendancemanager/ui/navigation/NavGraph.kt`
- Uses `LaunchedEffect` to automatically navigate to the reset password screen when a deep link email is detected
- The navigation route: `reset-password?email={email}`

### 5. Reset Password Screen
- Located in: `app/src/main/java/com/projects/attendancemanager/ui/view/ResetNewPasswordScreen.kt`
- Receives the email parameter and displays it to the user
- Allows the user to enter and confirm their new password
- Submits the new password to the backend API

## Testing the Deep Link

### Method 1: Using the Test HTML File
1. Open `test_deep_link.html` in a web browser on your Android device
2. Click one of the test links
3. Your device should prompt you to open the link with the Attendance Manager app
4. The app should open directly to the ResetNewPasswordScreen

### Method 2: Using ADB (Android Debug Bridge)
```bash
adb shell am start \
  -W -a android.intent.action.VIEW \
  -d "http://Attendance_Manager/app1/reset-password?email=test@example.com" \
  com.projects.attendancemanager
```

### Method 3: Manual Testing
1. Send yourself a real password reset email
2. Open the email on your Android device
3. Click the reset link
4. Verify the app opens to the correct screen

## Troubleshooting

### Common Issues:

1. **App doesn't open when clicking the link**
   - Ensure the app is installed on the device
   - Check that the AndroidManifest.xml intent filter is correctly configured
   - Verify the URL scheme matches exactly

2. **App opens but doesn't navigate to reset screen**
   - Check the deep link handling logic in MainActivity
   - Verify the NavGraph navigation logic
   - Look for any navigation conflicts

3. **Email parameter not passed correctly**
   - Ensure the URL format in resetPassword.js matches the expected format
   - Check the parameter extraction logic in MainActivity

### Debug Steps:

1. Add logging to MainActivity's `handleDeepLink()` method to see what URLs are being received
2. Check Android's logcat for any navigation errors
3. Test with the HTML file first before testing with actual emails

## Security Considerations

1. **URL Validation**: The app should validate that the email parameter is properly formatted
2. **Authentication**: Consider adding token-based authentication to the reset links
3. **Expiration**: Reset links should have an expiration time
4. **Rate Limiting**: Implement rate limiting for password reset requests

## Future Enhancements

1. **Custom URL Scheme**: Consider using a custom scheme like `attendancemanager://` instead of `http://`
2. **Token Validation**: Add token-based validation for reset links
3. **Universal Links**: Implement Android App Links for better user experience
4. **Fallback Handling**: Provide a web-based fallback if the app is not installed

## API Endpoints

- **Request Reset**: `POST /api/reset-password`
  - Body: `{ "email": "user@example.com" }`
  
- **Submit New Password**: `POST /api/reset-password/submit`
  - Body: `{ "email": "user@example.com", "newPassword": "newpass123" }`

## File Structure

```
attendance_backend/
├── resetPassword.js          # Email sending logic
└── server.js                # API endpoints

app/src/main/
├── AndroidManifest.xml       # Deep link configuration
├── java/com/projects/attendancemanager/
│   ├── MainActivity.kt       # Deep link handling
│   ├── ui/navigation/
│   │   └── NavGraph.kt      # Navigation setup
│   ├── ui/view/
│   │   └── ResetNewPasswordScreen.kt  # Reset password UI
│   └── network/
│       └── UserApiService.kt # API service definitions
```

This setup provides a seamless user experience where clicking a password reset link in an email directly opens the mobile app to the appropriate screen.