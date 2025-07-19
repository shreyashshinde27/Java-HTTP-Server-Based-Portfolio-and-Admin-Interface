const express = require('express');
const router = express.Router();
const ContactMessage = require('../models/ContactMessage');

// POST a new contact message
router.post('/contact', async (req, res) => {
  try {
    const { name, email, message } = req.body;

    if (!name || !email || !message) {
      return res.status(400).json({ error: 'All fields are required' });
    }

    const newMessage = new ContactMessage({ name, email, message });
    await newMessage.save();
    res.status(201).json({ message: 'Message received! Thank you.' });
  } catch (err) {
    console.error('Error saving contact message:', err);
    res.status(500).json({ error: 'Server error while saving message' });
  }
});

module.exports = router;