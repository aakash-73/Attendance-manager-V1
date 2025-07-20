const { google } = require('googleapis');
const nodemailer = require('nodemailer');

const oAuth2Client = new google.auth.OAuth2(
  process.env.OAUTH_CLIENT_ID,
  process.env.OAUTH_CLIENT_SECRET,
  process.env.OAUTH_REDIRECT_URI
);

oAuth2Client.setCredentials({ refresh_token: process.env.OAUTH_REFRESH_TOKEN });

async function getAccessToken() {
  try {
    const accessTokenResponse = await oAuth2Client.getAccessToken();
    if (!accessTokenResponse || !accessTokenResponse.token) {
      throw new Error('Failed to retrieve access token');
    }
    return accessTokenResponse.token;
  } catch (error) {
    console.error('❌ Error fetching access token:', error.message);
    throw error;
  }
}

async function sendResetEmail(email, name) {
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
        accessToken: accessToken,
      },
    });

    const resetLink = `${process.env.LINK_URL}/reset-password?email=${encodeURIComponent(email)}`;

    const mailOptions = {
      from: `"Attendance Manager" <${process.env.EMAIL_SENDER}>`,
      to: email,
      subject: 'Reset Your Password',
      html: `
        <p>Hi ${name},</p>
        <p>Click the link below to reset your password:</p>
        <a href="${resetLink}">${resetLink}</a>
        <p>If you did not request this, you can safely ignore this email.</p>
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
