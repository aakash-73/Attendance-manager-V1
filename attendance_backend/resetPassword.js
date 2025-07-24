const { google } = require('googleapis');
const nodemailer = require('nodemailer');

// Initialize OAuth2 client
const oAuth2Client = new google.auth.OAuth2(
  process.env.OAUTH_CLIENT_ID,
  process.env.OAUTH_CLIENT_SECRET,
  process.env.OAUTH_REDIRECT_URI // Usually https://developers.google.com/oauthplayground
);

// Set the refresh token once
oAuth2Client.setCredentials({
  refresh_token: process.env.OAUTH_REFRESH_TOKEN,
});

// Get a new access token using the refresh token
async function getAccessToken() {
  try {
    const accessTokenResponse = await oAuth2Client.getAccessToken();
    if (!accessTokenResponse || !accessTokenResponse.token) {
      throw new Error('Failed to retrieve access token from Google');
    }
    return accessTokenResponse.token;
  } catch (error) {
    console.error('❌ Error fetching access token:', error.message);
    throw error;
  }
}

// Send reset password email
async function sendResetEmail(email, name = 'User') {
  try {
    const accessToken = await getAccessToken();

    const transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        type: 'OAuth2',
        user: process.env.EMAIL_SENDER,
        clientId: process.env.OAUTH_CLIENT_ID,
        clientSecret: process.env.OAUTH_CLIENT_SECRET,
        refreshToken: process.env.OAUTH_REFRESH_TOKEN,
        accessToken,
      },
    });

    const resetLink = `${process.env.LINK_URL}/reset-password?email=${encodeURIComponent(email)}`;

    const mailOptions = {
      from: `"Attendance Manager" <${process.env.EMAIL_SENDER}>`,
      to: email,
      subject: 'Reset Your Password',
      html: `
        <div style="font-family: Arial, sans-serif; line-height: 1.6;">
          <p>Hi ${name},</p>
          <p>You requested to reset your password. Click the button below to continue:</p>
          <p>
            <a href="${resetLink}" style="display: inline-block; background-color: #0047AB; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
              Reset Password
            </a>
          </p>
          <p>Or copy and paste this link into your browser:</p>
          <p><a href="${resetLink}">${resetLink}</a></p>
          <p>If you didn't request this, you can ignore this email.</p>
        </div>
      `,
    };

    const result = await transporter.sendMail(mailOptions);
    console.log(`✅ Password reset email sent to ${email}`);
    return result;
  } catch (error) {
    console.error(`❌ Error sending reset email to ${email}:`, error.message);
    throw error;
  }
}

module.exports = { sendResetEmail };
