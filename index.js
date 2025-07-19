require('dotenv').config(); // Load environment variables

const express = require('express');
const mongoose = require('mongoose');
const ContactMessage = require('./models/ContactMessage');
const cors = require('cors');


const app = express();
const PORT = 3000;

// Set EJS as the view engine and set views directory
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');

// MongoDB connection
const mongoURI = process.env.MONGODB_URI;

mongoose.connect(mongoURI);

// Connection events
mongoose.connection.on('connected', () => {
  console.log('Mongoose connected to MongoDB');
});

mongoose.connection.on('error', (err) => {
  console.error('Mongoose connection error:', err);
});

mongoose.connection.on('disconnected', () => {
  console.log('Mongoose disconnected from MongoDB');
});

// Serve static files from the 'public' directory
app.use(express.static('public'));

app.use(express.json());

const corsOptions = {
  origin: [
    'http://localhost:3000',
    'http://localhost:50891',
    'http://192.168.56.1:50891'
  ],
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Admin-Token'],
  credentials: true
};

app.use(cors(corsOptions));

const contactRoutes = require('./routes/contact');
const adminRoutes = require('./routes/admin');

app.use('/api', contactRoutes);
app.use('/api/admin', adminRoutes);

// Optional: Global error handler
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Something went wrong!' });
});

// Basic route
app.get('/', (req, res) => {
  res.send('Express server is running and connected to MongoDB!');
});

app.listen(PORT, () => {
  console.log(`Server is running on http://localhost:${PORT}`);
});