// Admin Dashboard JS
let adminToken = window.adminToken || '';
const loginSection = document.getElementById('login-section');
const dashboardSection = document.getElementById('dashboard-section');
const loginBtn = document.getElementById('login-btn');
const adminTokenInput = document.getElementById('admin-token');
const loginError = document.getElementById('login-error');

const messagesTableBody = document.getElementById('messages-table-body');

const API_BASE = 'http://localhost:3000';


loginBtn.addEventListener('click', async () => {
    adminToken = adminTokenInput.value.trim();
    if (!adminToken) {
        loginError.textContent = 'Please enter the admin token.';
        loginError.style.display = 'block';
        return;
    }
    // Try to fetch messages to verify token
    const ok = await fetchMessages();
    if (ok) {
        loginSection.style.display = 'none';
        dashboardSection.style.display = 'block';
    } else {
        loginError.textContent = 'Invalid admin token.';
        loginError.style.display = 'block';
    }
});

async function fetchMessages() {
    try {
        const res = await fetch(`${API_BASE}/api/admin/messages`, {
            headers: { 'X-Admin-Token': adminToken }
        });
        if (!res.ok) return false;
        const messages = await res.json();
        renderMessages(messages);
        return true;
    } catch (err) {
        return false;
    }
}

function renderMessages(messages) {
    messagesTableBody.innerHTML = '';
    if (!messages.length) {
        messagesTableBody.innerHTML = '<tr><td colspan="6" class="text-center">No messages found.</td></tr>';
        return;
    }
    messages.forEach(msg => {
        const tr = document.createElement('tr');
        tr.setAttribute('data-id', msg.id);
        tr.innerHTML = `
            <td>${msg.id}</td>
            <td class="msg-name">${msg.name}</td>
            <td class="msg-email">${msg.email}</td>
            <td class="msg-message">${msg.message}</td>
            <td class="msg-date">${msg.created_at}</td>
            <td>
                <button class="btn btn-warning btn-sm me-1" onclick="editMessage('${msg.id}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteMessage('${msg.id}')">Delete</button>
            </td>
        `;
        messagesTableBody.appendChild(tr);
    });
}

window.viewMessage = async function(id) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/messages/${id}`, {
            headers: { 'X-Admin-Token': adminToken }
        });
        if (!res.ok) throw new Error();
        const msg = await res.json();
        document.getElementById('viewModalBody').innerHTML = `
            <strong>ID:</strong> ${msg.id}<br>
            <strong>Name:</strong> ${msg.name}<br>
            <strong>Email:</strong> ${msg.email}<br>
            <strong>Message:</strong> <pre>${msg.message}</pre><br>
            <strong>Date:</strong> ${msg.created_at}
        `;
        new bootstrap.Modal(document.getElementById('viewModal')).show();
    } catch {
        alert('Failed to load message.');
    }
}

window.editMessage = async function(id) {
    try {
        console.log('Edit: using adminToken:', adminToken);
        const res = await fetch(`${API_BASE}/api/admin/messages/${id}`, {
            headers: { 'X-Admin-Token': adminToken }
        });
        console.log('Edit response status:', res.status);
        if (!res.ok) throw new Error();
        const msg = await res.json();
        document.getElementById('edit-id').value = msg.id;
        document.getElementById('edit-name').value = msg.name;
        document.getElementById('edit-email').value = msg.email;
        document.getElementById('edit-message').value = msg.message;
        new bootstrap.Modal(document.getElementById('editModal')).show();
    } catch {
        alert('Failed to load message for editing.');
    }
}

document.getElementById('editForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const id = document.getElementById('edit-id').value;
    const name = document.getElementById('edit-name').value.trim();
    const email = document.getElementById('edit-email').value.trim();
    const message = document.getElementById('edit-message').value.trim();
    try {
        console.log('Edit submit: using adminToken:', adminToken);
        const res = await fetch(`${API_BASE}/api/admin/messages/${id}`, {
            method: 'PUT',
            headers: {
                'X-Admin-Token': adminToken,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, email, message })
        });
        console.log('Edit submit response status:', res.status);
        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById('editModal')).hide();
            const row = messagesTableBody.querySelector(`tr[data-id='${id}']`);
            if (row) {
                row.querySelector('.msg-name').textContent = name;
                row.querySelector('.msg-email').textContent = email;
                row.querySelector('.msg-message').textContent = message;
            }
            showAlert('Message updated successfully!', 'success');
        } else {
            showAlert('Failed to update message.', 'danger');
        }
    } catch {
        showAlert('Failed to update message.', 'danger');
    }
});

function showAlert(message, type) {
    let alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.role = 'alert';
    alert.textContent = message;
    dashboardSection.insertBefore(alert, dashboardSection.firstChild);
    setTimeout(() => {
        alert.remove();
    }, 4000);
}

window.deleteMessage = async function(id) {
    if (!confirm('Are you sure you want to delete this message?')) return;
    try {
        console.log('Delete: using adminToken:', adminToken);
        const res = await fetch(`${API_BASE}/api/admin/messages/${id}`, {
            method: 'DELETE',
            headers: { 'X-Admin-Token': adminToken }
        });
        console.log('Delete response status:', res.status);
        if (res.ok) {
            const row = messagesTableBody.querySelector(`tr[data-id='${id}']`);
            if (row) row.remove();
            showAlert('Message deleted successfully!', 'success');
        } else {
            showAlert('Failed to delete message.', 'danger');
        }
    } catch (err) {
        showAlert('Failed to delete message.', 'danger');
    }
} 