// Smooth scrolling for navigation links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Navbar background change on scroll
window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});

// Form submission handling
const contactForm = document.getElementById('contact-form');
if (contactForm) {
    contactForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        // Get form data
        const name = contactForm.querySelector('input[placeholder="Your Name"]').value.trim();
        const email = contactForm.querySelector('input[placeholder="Your Email"]').value.trim();
        const message = contactForm.querySelector('textarea[placeholder="Your Message"]').value.trim();

        // Prepare JSON
        const data = { name, email, message };

        try {
            const response = await fetch('/api/contact', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            if (response.ok && result.success) {
                showAlert('Thank you for your message! I will get back to you soon.', 'success');
                contactForm.reset();
            } else {
                showAlert(result.error || 'There was a problem submitting your message.', 'danger');
            }
        } catch (err) {
            showAlert('There was a problem submitting your message.', 'danger');
        }
    });
}

// Helper to show alert messages
function showAlert(message, type) {
    let alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.role = 'alert';
    alert.textContent = message;
    contactForm.parentNode.insertBefore(alert, contactForm);
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

// Animate progress bars on scroll
const animateProgressBars = () => {
    const progressBars = document.querySelectorAll('.progress-bar');
    progressBars.forEach(bar => {
        const width = bar.style.width;
        bar.style.width = '0';
        setTimeout(() => {
            bar.style.width = width;
        }, 200);
    });
};

// Intersection Observer for progress bars
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            animateProgressBars();
            observer.unobserve(entry.target);
        }
    });
}, { threshold: 0.5 });

// Observe the skills section
const skillsSection = document.querySelector('.skills');
if (skillsSection) {
    observer.observe(skillsSection);
}

// Typewriter effect
const typewriter = document.getElementById('typewriter');
const texts = ['Full Stack Development', 'Data Science']; // Removed the extra '|'
let textIndex = 0;
let charIndex = 0;
let isDeleting = false;
let typingDelay = 100;
let erasingDelay = 50;
let newTextDelay = 2000;

function type() {
    const currentText = texts[textIndex];
    
    if (isDeleting) {
        typewriter.textContent = currentText.substring(0, charIndex - 1);
        charIndex--;
        typingDelay = erasingDelay;
    } else {
        typewriter.textContent = currentText.substring(0, charIndex + 1);
        charIndex++;
        typingDelay = 100;
    }

    if (!isDeleting && charIndex === currentText.length) {
        isDeleting = true;
        typingDelay = newTextDelay;
    } else if (isDeleting && charIndex === 0) {
        isDeleting = false;
        textIndex = (textIndex + 1) % texts.length;
        typingDelay = 500;
    }

    setTimeout(type, typingDelay);
}

// Start the typewriter effect when the page loads
document.addEventListener('DOMContentLoaded', () => {
    // Clear any existing content
    typewriter.textContent = '';
    // Start the animation
    type();
});