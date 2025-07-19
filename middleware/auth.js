function adminAuth(req, res, next) {
  // Check for Authorization header first (Bearer token)
  const authHeader = req.headers['authorization'];
  let token = null;
  // Try Authorization: Bearer <token>
  if (authHeader && authHeader.startsWith('Bearer ')) {
    token = authHeader.substring(7);
  } else {
    // Fallback to x-admin-token (case-insensitive)
    token = req.headers['x-admin-token'] || req.headers['X-Admin-Token'] || req.headers['x-admin-token'.toLowerCase()] || req.headers['x-admin-token'.toUpperCase()];
    // Or check all headers for a match (case-insensitive)
    if (!token) {
      for (const key in req.headers) {
        if (key.toLowerCase() === 'x-admin-token') {
          token = req.headers[key];
          break;
        }
      }
    }
  }

  if (!token) {
    return res.status(401).json({ error: 'Access denied. No token provided.' });
  }

  if (token !== process.env.ADMIN_SECRET_TOKEN) {
    return res.status(403).json({ error: 'Invalid token.' });
  }

  next();
}

module.exports = { adminAuth };