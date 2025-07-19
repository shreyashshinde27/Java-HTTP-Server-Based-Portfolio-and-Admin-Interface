const express = require('express');
const router = express.Router();
const { adminAuth } = require('../middleware/auth');
const ContactMessage = require('../models/ContactMessage');

// Admin Login Route
// This route is for the frontend to verify the token.
// The real protection is the adminAuth middleware on other routes.
router.post('/login', (req, res) => {
  const { secretToken } = req.body;
  if (secretToken && secretToken === process.env.ADMIN_SECRET_TOKEN) {
    res.status(200).json({ message: 'Login successful' });
  } else {
    res.status(401).json({ error: 'Invalid admin token' });
  }
});

// Admin Dashboard Route (protected) - Renders the main admin page
router.get('/', adminAuth, async (req, res) => {
  try {
    // Assuming you have a view engine set up and an 'admin.ejs' or similar file
    res.render('admin', { projects: [] }); // Pass an empty array as projects are removed
  } catch (err) {
    console.error(err);
    res.status(500).send('Server error');
  }
});

// GET all contact messages (Protected)
router.get('/messages', adminAuth, async (req, res) => {
  try {
    const messages = await ContactMessage.find().sort({ date: -1 });
    // Map to expected frontend format
    const mapped = messages.map(msg => ({
      id: msg._id.toString(),
      name: msg.name,
      email: msg.email,
      message: msg.message,
      created_at: msg.date ? new Date(msg.date).toLocaleString() : ''
    }));
    res.json(mapped);
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
});

// GET a single contact message (Protected)
router.get('/messages/:id', adminAuth, async (req, res) => {
  try {
    const msg = await ContactMessage.findById(req.params.id);
    if (!msg) return res.status(404).json({ error: 'Message not found' });
    res.json({
      id: msg._id.toString(),
      name: msg.name,
      email: msg.email,
      message: msg.message,
      created_at: msg.date ? new Date(msg.date).toLocaleString() : ''
    });
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
});

// UPDATE a message (Protected)
router.put('/messages/:id', adminAuth, async (req, res) => {
  try {
    const msg = await ContactMessage.findById(req.params.id);
    if (!msg) return res.status(404).json({ error: 'Message not found' });
    msg.name = req.body.name;
    msg.email = req.body.email;
    msg.message = req.body.message;
    await msg.save();
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
});

// DELETE a contact message (Protected)
router.delete('/messages/:id', adminAuth, async (req, res) => {
  try {
    const msg = await ContactMessage.findByIdAndDelete(req.params.id);
    if (!msg) return res.status(404).json({ error: 'Message not found' });
    res.json({ success: true });
  } catch (err) {
    res.status(500).json({ error: 'Server error' });
  }
});

module.exports = router;