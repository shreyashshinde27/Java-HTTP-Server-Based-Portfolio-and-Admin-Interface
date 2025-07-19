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

// API base URL
const API_BASE = 'http://localhost:3000';

// Form submission handling for both possible form IDs
const contactForm = document.getElementById('contact-form') || document.getElementById('contactForm');
if (contactForm) {
    contactForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        let data;
        if (contactForm.id === 'contact-form') {
            const formData = new FormData(contactForm);
            data = Object.fromEntries(formData.entries());
        } else {
            const name = document.getElementById('name').value.trim();
            const email = document.getElementById('email').value.trim();
            const message = document.getElementById('message').value.trim();
            data = { name, email, message };
        }
        const submitBtn = contactForm.querySelector('button[type="submit"]');
        const originalText = submitBtn ? submitBtn.textContent : '';
        if (submitBtn) {
            submitBtn.textContent = 'Sending...';
            submitBtn.disabled = true;
        }
        try {
            const response = await fetch(`${API_BASE}/api/contact`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            if (response.ok) {
                window.location.href = '/success.html';
            } else {
                showAlert('Failed to send message. Please try again.', 'danger');
            }
        } catch (error) {
            showAlert('An error occurred. Please try again later.', 'danger');
        } finally {
            if (submitBtn) {
                submitBtn.textContent = originalText;
                submitBtn.disabled = false;
            }
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

type();