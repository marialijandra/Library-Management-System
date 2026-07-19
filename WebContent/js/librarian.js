let books = [
    { id: 1, title: "To Kill a Mockingbird", description: "A moral coming-of-age story set in the Depression-era South. Told through the eyes of a young girl.", quantity: 4, image: "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1612238791i/56916837.jpg" },
    { id: 2, title: "Designing Data-Intensive Applications", description: "A deep dive into the ideas behind reliable, scalable data systems. A staple for backend engineers.", quantity: 3, image: "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1489957987i/34626431.jpg" },
    { id: 3, title: "Pride and Prejudice", description: "A witty exploration of manners and marriage in Regency England. Follows the spirited Elizabeth Bennet.", quantity: 5, image: "https://m.media-amazon.com/images/I/81Scutrtj4L._UF1000,1000_QL80_.jpg" },
    { id: 4, title: "1984", description: "A dystopian vision of a totalitarian surveillance state. A warning about the erosion of truth and freedom.", quantity: 6, image: "https://www.penguin.co.uk/_next/image?url=https%3A%2F%2Fcdn.penguin.co.uk%2Fdam-assets%2Fbooks%2F9780141036144%2F9780141036144-jacket-large.jpg&w=614&q=100" },
    { id: 5, title: "The Great Gatsby", description: "A glittering, tragic portrait of the Jazz Age and the American Dream. Narrated by the observant Nick Carraway.", quantity: 4, image: "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1650033243i/41733839.jpg" },
    { id: 6, title: "Clean Code", description: "A handbook of practices for writing readable, maintainable software. Widely used in software engineering courses.", quantity: 3, image: "" },
    { id: 7, title: "The Phoenix Project", description: "A novel about IT, DevOps, and helping a business survive a crisis. Popular in software delivery circles.", quantity: 2, image: "" },
    { id: 8, title: "Moby Dick", description: "An epic tale of obsession aboard a whaling ship. Follows Captain Ahab's pursuit of a great white whale.", quantity: 3, image: "" },
    { id: 9, title: "War and Peace", description: "A sweeping account of Russian society during the Napoleonic era. Follows several interconnected families.", quantity: 2, image: "" },
    { id: 10, title: "The Odyssey", description: "An ancient epic following a hero's long journey home after war. A foundational work of Western literature.", quantity: 4, image: "" }
];

// Transaction tab data now comes live from TransactionServlet (see fetchBorrowers()
// / fetchTransactionBooks() below) instead of being mocked here.
let borrowers = [];
let transactionBooks = []; // { id, title, quantity } - live from the DB, used for the "select a book" dropdowns

let editingBookId = null;
let newBookEntryCount = 0;
let activeBorrowerId = null;      // userId (string) of the borrower currently open in the modal
let activeBorrowerLoanIds = [];   // transactionId for each row rendered in #borrowerLoanRows, in order

// librarian.js is loaded from a page under /views/ (e.g. .../views/libraryManager.jsp),
// but TransactionServlet is mapped at the app root, not under /views/. So the URL
// has to go "up" one level, the same way the JSP does for ../images, ../css, etc.
const TRANSACTION_SERVLET = '../TransactionServlet';

// Shared response handler for all TransactionServlet calls below. Without this,
// a 403 (expired/missing session) still has a valid JSON body - {"error":"..."}
// - so res.json() would succeed and hand callers an object where they expect
// an array (borrowers/transactionBooks), crashing renderBorrowers()/etc. with
// "borrowers.filter is not a function" instead of showing anything useful.
function handleTransactionResponse(res) {
    if (res.status === 401 || res.status === 403) {
        alert('Your session has expired or you are not logged in. Please log in again.');
        window.location.href = '../views/login.jsp';
        return new Promise(function () {}); // navigating away - let this hang, don't resolve/reject
    }
    if (!res.ok) {
        throw new Error('Request failed with status ' + res.status);
    }
    return res.json();
}

function fetchBorrowers() {
    return fetch(TRANSACTION_SERVLET + '?action=list')
        .then(handleTransactionResponse)
        .then(function (data) {
            borrowers = data;
            renderBorrowers();
            renderDashboard();
        })
        .catch(function (err) {
            console.error('Failed to load borrowers:', err);
        });
}

function fetchTransactionBooks() {
    return fetch(TRANSACTION_SERVLET + '?action=books')
        .then(handleTransactionResponse)
        .then(function (data) {
            transactionBooks = data;
            books = data;
        })
        .catch(function (err) {
            console.error('Failed to load books for transactions:', err);
        });
}

function postTransactionAction(params) {
    return fetch(TRANSACTION_SERVLET, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    }).then(handleTransactionResponse);
}

function switchTab(tabName) {
    document.querySelectorAll('.tab-view').forEach(function (el) { el.classList.remove('active'); });
    document.querySelectorAll('.side-nav a').forEach(function (el) { el.classList.remove('active'); });
    document.getElementById('tab-' + tabName).classList.add('active');
    document.getElementById('nav-' + tabName).classList.add('active');

    var mainContent = document.getElementById('mainContent');
    if (tabName === 'dashboard') {
        mainContent.classList.add('no-scroll');
        renderDashboard();
    } else {
        mainContent.classList.remove('no-scroll');
    }
}

function renderBooks(filter) {
    var tbody = document.getElementById('booksTableBody');
    tbody.innerHTML = '';
    var term = (filter || '').toLowerCase();

    books.filter(function (b) {
        return b.title.toLowerCase().indexOf(term) !== -1;
    }).forEach(function (b) {
        var tr = document.createElement('tr');
        tr.className = 'clickable';
        tr.onclick = function () { openEditBookModal(b.id); };

        var coverStyle = b.image ? "background-image:url('" + b.image + "')" : "";

        tr.innerHTML =
            '<td><div class="title-cell">' +
            '<span class="book-cover-thumb" style="' + coverStyle + '"></span>' +
            '<span>' + escapeHtml(b.title) + '</span>' +
            '</div></td>' +
            '<td>' + b.quantity + '</td>' +
            '<td>' + escapeHtml(b.description) + '</td>' +
            '<td onclick="event.stopPropagation();">' +
            '<button class="icon-btn" onclick="openEditBookModal(' + b.id + ')" title="Edit">&#9998;</button> ' +
            '<button class="icon-btn delete" onclick="deleteBook(' + b.id + ')" title="Delete">&#128465;</button>' +
            '</td>';
        tbody.appendChild(tr);
    });
}

function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function searchBooks() {
    var val = document.getElementById('bookSearchInput').value;
    renderBooks(val);
}

function openEditBookModal(bookId) {
    var book = books.find(function (b) { return b.id == bookId; });
    if (!book) return;
    editingBookId = bookId;

    document.getElementById('editBookTitle').value = book.title;
    document.getElementById('editBookDescription').value = book.description;
    document.getElementById('editBookQuantity').value = book.quantity;
    document.getElementById('editBookImage').value = book.image;

    document.getElementById('editBookModal').classList.add('open');
}

function closeEditBookModal() {
    document.getElementById('editBookModal').classList.remove('open');
    editingBookId = null;
}

function saveEditedBook() {
    var book = books.find(function (b) { return b.id == editingBookId; });
    if (!book) return;

    var title = document.getElementById('editBookTitle').value.trim() || book.title;
    var description = document.getElementById('editBookDescription').value.trim();
    var quantity = parseInt(document.getElementById('editBookQuantity').value, 10) || 0;
    var image = document.getElementById('editBookImage').value.trim();

    var params = new URLSearchParams();
    params.append('id', editingBookId);
    params.append('title', title);
    params.append('description', description);
    params.append('quantity', quantity);
    params.append('imageUrl', image);

    fetch('../books?action=update', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
    })
    .then(function (res) { return res.json(); })
    .then(function (data) {
        if (!data.success) {
            alert(data.message || 'Failed to save edited book.');
            return;
        }
        closeEditBookModal();
        fetchTransactionBooks().then(function () {
            renderBooks();
            renderDashboard();
        });
    })
    .catch(function (err) {
        console.error('Failed to save edited book:', err);
        alert('Failed to save edited book. Please try again.');
    });
}

function deleteBook(bookId) {
    if (!confirm('Delete this book from the catalog?')) return;
    
    fetch('../books?action=delete&id=' + bookId, {
        method: 'POST'
    })
    .then(function (res) { return res.json(); })
    .then(function (data) {
        if (!data.success) {
            alert(data.message || 'Failed to delete book.');
            return;
        }
        fetchTransactionBooks().then(function () {
            renderBooks();
            renderDashboard();
        });
    })
    .catch(function (err) {
        console.error('Failed to delete book:', err);
        alert('Failed to delete book. Please try again.');
    });
}

function deleteBookFromModal() {
    if (editingBookId == null) return;
    deleteBook(editingBookId);
    closeEditBookModal();
}

function openAddBookModal() {
    document.getElementById('addBookEntries').innerHTML = '';
    newBookEntryCount = 0;
    addBookEntryBlock();
    document.getElementById('addBookModal').classList.add('open');
}

function closeAddBookModal() {
    document.getElementById('addBookModal').classList.remove('open');
}

function addBookEntryBlock() {
    newBookEntryCount++;
    var idx = newBookEntryCount;
    var container = document.getElementById('addBookEntries');

    var block = document.createElement('div');
    block.className = 'book-entry-block';
    block.id = 'newBookEntry-' + idx;
    block.innerHTML =
        '<button type="button" class="remove-entry" onclick="removeBookEntryBlock(' + idx + ')">Remove</button>' +
        '<div class="mf-group"><label>Title</label><input type="text" id="newBookTitle-' + idx + '" placeholder="Book title"></div>' +
        '<div class="mf-group"><label>Description</label><textarea id="newBookDescription-' + idx + '" placeholder="Short two-sentence description"></textarea></div>' +
        '<div class="mf-group"><label>Quantity</label><input type="number" id="newBookQuantity-' + idx + '" min="0" placeholder="0"></div>' +
        '<div class="mf-group"><label>Image URL</label><input type="text" id="newBookImage-' + idx + '" placeholder="https://..."></div>';

    container.appendChild(block);
    updateRemoveButtons();
}

function removeBookEntryBlock(idx) {
    var el = document.getElementById('newBookEntry-' + idx);
    if (el) el.remove();
    updateRemoveButtons();
}

function updateRemoveButtons() {
    var blocks = document.querySelectorAll('#addBookEntries .book-entry-block');
    blocks.forEach(function (block) {
        var btn = block.querySelector('.remove-entry');
        btn.style.display = blocks.length > 1 ? 'block' : 'none';
    });
}

function saveNewBooks() {
    var blocks = document.querySelectorAll('#addBookEntries .book-entry-block');
    var promises = [];

    blocks.forEach(function (block) {
        var idx = block.id.split('-')[1];
        var title = document.getElementById('newBookTitle-' + idx).value.trim();
        if (!title) return;
        var description = document.getElementById('newBookDescription-' + idx).value.trim();
        var quantity = parseInt(document.getElementById('newBookQuantity-' + idx).value, 10) || 0;
        var image = document.getElementById('newBookImage-' + idx).value.trim();

        var params = new URLSearchParams();
        params.append('title', title);
        params.append('description', description);
        params.append('quantity', quantity);
        params.append('imageUrl', image);

        var p = fetch('../books?action=insert', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params.toString()
        })
        .then(function (res) { return res.json(); })
        .then(function (data) {
            if (!data.success) {
                alert(data.message || ('Failed to add book: ' + title));
                throw new Error(data.message || ('Failed to add book: ' + title));
            }
        });
        promises.push(p);
    });

    if (promises.length === 0) return;

    Promise.all(promises).then(function () {
        closeAddBookModal();
        fetchTransactionBooks().then(function () {
            renderBooks();
            renderDashboard();
        });
    }).catch(function (err) {
        console.error('Failed to save new books:', err);
        // Refresh the list of books in the dashboard in case some of the entries succeeded
        fetchTransactionBooks().then(function () {
            renderBooks();
            renderDashboard();
        });
    });
}

function renderBorrowers(filter) {
    var tbody = document.getElementById('borrowersTableBody');
    tbody.innerHTML = '';
    var term = (filter || '').toLowerCase();

    borrowers.filter(function (p) {
        var fullName = (p.firstName + ' ' + p.surname).toLowerCase();
        return fullName.indexOf(term) !== -1 || p.email.toLowerCase().indexOf(term) !== -1;
    }).forEach(function (p) {
        var tr = document.createElement('tr');
        tr.className = 'clickable';
        tr.onclick = function () { openBorrowerModal(p.userId); };

        var stillOut = p.loans.filter(function (l) { return l.status === 'borrowed'; }).length;
        var statusHtml = stillOut > 0
            ? '<span class="status-pill borrowed">' + stillOut + ' still borrowed</span>'
            : '<span class="status-pill returned">All returned</span>';

        tr.innerHTML =
            '<td>' + escapeHtml(p.firstName + ' ' + p.surname) + '</td>' +
            '<td>' + escapeHtml(p.email) + '</td>' +
            '<td>' + p.loans.length + ' book(s)</td>' +
            '<td>' + statusHtml + '</td>';
        tbody.appendChild(tr);
    });
}

function searchBorrowers() {
    var val = document.getElementById('borrowerSearchInput').value;
    renderBorrowers(val);
}

var borrowerNewBookRowCount = 0;

function openBorrowerModal(userId) {
    var p = borrowers.find(function (b) { return b.userId === userId; });
    if (!p) return;
    activeBorrowerId = userId;

    document.getElementById('borrowerModalName').textContent = p.firstName + ' ' + p.surname;

    fetchTransactionBooks().then(function () {
        var rows = document.getElementById('borrowerLoanRows');
        rows.innerHTML = '';
        activeBorrowerLoanIds = [];

        p.loans.forEach(function (loan, i) {
            activeBorrowerLoanIds.push(loan.transactionId);

            var row = document.createElement('div');
            row.className = 'status-select-row';
            row.innerHTML =
                '<span>' + escapeHtml(loan.bookTitle) + '</span>' +
                '<select id="loanStatus-' + i + '">' +
                '<option value="borrowed"' + (loan.status === 'borrowed' ? ' selected' : '') + '>Still Borrowed</option>' +
                '<option value="returned"' + (loan.status === 'returned' ? ' selected' : '') + '>Returned</option>' +
                '</select>' +
                '<button type="button" class="icon-btn delete" title="Delete" ' +
                'onclick="deleteLoanRow(\'' + loan.transactionId + '\')">&#128465;</button>';
            rows.appendChild(row);
        });

        document.getElementById('borrowerNewBookRows').innerHTML = '';
        borrowerNewBookRowCount = 0;

        document.getElementById('borrowerModal').classList.add('open');
    });
}

function addBorrowerLoanBookRow() {
    borrowerNewBookRowCount++;
    var idx = borrowerNewBookRowCount;
    var container = document.getElementById('borrowerNewBookRows');

    var row = document.createElement('div');
    row.className = 'borrower-book-row';
    row.id = 'borrowerNewBookRow-' + idx;

    var select = document.createElement('select');
    select.id = 'borrowerNewBook-' + idx;
    select.innerHTML = '<option value="" disabled selected>Select a book</option>';
    transactionBooks.forEach(function (b) {
        var opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = b.title + (b.quantity <= 0 ? ' (none available)' : '');
        if (b.quantity <= 0) opt.disabled = true;
        select.appendChild(opt);
    });

    var removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'remove-row-btn';
    removeBtn.style.display = 'block';
    removeBtn.textContent = '×';
    removeBtn.onclick = function () { row.remove(); };

    row.appendChild(select);
    row.appendChild(removeBtn);
    container.appendChild(row);
}

function closeBorrowerModal() {
    document.getElementById('borrowerModal').classList.remove('open');
    activeBorrowerId = null;
    activeBorrowerLoanIds = [];
}

function deleteLoanRow(transactionId) {
    if (!confirm('Delete this loan record? If the book is still borrowed, the copy will be returned to stock.')) return;

    var params = new URLSearchParams();
    params.append('action', 'deleteLoan');
    params.append('transactionId', transactionId);

    postTransactionAction(params).then(function (result) {
        if (!result.success && result.messages && result.messages.length) {
            alert(result.messages.join('\n'));
        }
        var reopenId = activeBorrowerId;
        fetchBorrowers().then(function () {
            closeBorrowerModal();
            if (reopenId && borrowers.some(function (b) { return b.userId === reopenId; })) {
                openBorrowerModal(reopenId);
            }
        });
    }).catch(function (err) {
        console.error(err);
        alert('Something went wrong deleting that loan.');
    });
}

function saveBorrowerStatuses() {
    if (!activeBorrowerId) return;

    var params = new URLSearchParams();
    params.append('action', 'updateBorrower');
    params.append('userId', activeBorrowerId);

    activeBorrowerLoanIds.forEach(function (transactionId, i) {
        var select = document.getElementById('loanStatus-' + i);
        if (select) {
            params.append('transactionIds', transactionId);
            params.append('statuses', select.value);
        }
    });

    document.querySelectorAll('#borrowerNewBookRows select').forEach(function (sel) {
        if (sel.value) params.append('newBookIds', sel.value);
    });

    postTransactionAction(params).then(function (result) {
        if (result.messages && result.messages.length) {
            alert(result.messages.join('\n'));
        }
        closeBorrowerModal();
        fetchBorrowers();
    }).catch(function (err) {
        console.error(err);
        alert('Something went wrong saving those changes.');
    });
}

var newBorrowerBookRowCount = 0;

function openAddBorrowerModal() {
    document.getElementById('newBorrowerFirstName').value = '';
    document.getElementById('newBorrowerSurname').value = '';
    document.getElementById('newBorrowerEmail').value = '';
    document.getElementById('newBorrowerBookRows').innerHTML = '';
    newBorrowerBookRowCount = 0;

    fetchTransactionBooks().then(function () {
        addBorrowerBookRow();
        document.getElementById('addBorrowerModal').classList.add('open');
    });
}

function closeAddBorrowerModal() {
    document.getElementById('addBorrowerModal').classList.remove('open');
}

function addBorrowerBookRow() {
    newBorrowerBookRowCount++;
    var idx = newBorrowerBookRowCount;
    var container = document.getElementById('newBorrowerBookRows');

    var row = document.createElement('div');
    row.className = 'borrower-book-row';
    row.id = 'borrowerBookRow-' + idx;

    var select = document.createElement('select');
    select.id = 'newBorrowerBook-' + idx;
    select.innerHTML = '<option value="" disabled selected>Select a book</option>';
    transactionBooks.forEach(function (b) {
        var opt = document.createElement('option');
        opt.value = b.id;
        opt.textContent = b.title + (b.quantity <= 0 ? ' (none available)' : '');
        if (b.quantity <= 0) opt.disabled = true;
        select.appendChild(opt);
    });

    var removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'remove-row-btn';
    removeBtn.textContent = '×';
    removeBtn.onclick = function () { removeBorrowerBookRow(idx); };

    row.appendChild(select);
    row.appendChild(removeBtn);
    container.appendChild(row);
    updateBorrowerRowRemoveButtons();
}

function removeBorrowerBookRow(idx) {
    var el = document.getElementById('borrowerBookRow-' + idx);
    if (el) el.remove();
    updateBorrowerRowRemoveButtons();
}

function updateBorrowerRowRemoveButtons() {
    var rows = document.querySelectorAll('#newBorrowerBookRows .borrower-book-row');
    rows.forEach(function (row) {
        var btn = row.querySelector('.remove-row-btn');
        btn.style.display = rows.length > 1 ? 'block' : 'none';
    });
}

function saveNewBorrower() {
    var firstName = document.getElementById('newBorrowerFirstName').value.trim();
    var surname = document.getElementById('newBorrowerSurname').value.trim();
    var email = document.getElementById('newBorrowerEmail').value.trim();

    var bookIds = [];
    document.querySelectorAll('#newBorrowerBookRows select').forEach(function (sel) {
        if (sel.value) bookIds.push(sel.value);
    });

    if (!firstName || !surname || !email || bookIds.length === 0) {
        alert('Please fill out all fields and select at least one book.');
        return;
    }

    var params = new URLSearchParams();
    params.append('action', 'addBorrower');
    params.append('firstName', firstName);
    params.append('surname', surname);
    params.append('email', email);
    bookIds.forEach(function (id) { params.append('bookIds', id); });

    postTransactionAction(params).then(function (result) {
        if (!result.success) {
            alert(result.messages && result.messages.length ? result.messages.join('\n') : 'Could not add borrower.');
            return;
        }
        if (result.messages && result.messages.length) {
            alert(result.messages.join('\n'));
        }
        closeAddBorrowerModal();
        fetchBorrowers();
    }).catch(function (err) {
        console.error(err);
        alert('Something went wrong adding that borrower.');
    });
}

var donutChart = null;
var mostIssuedChart = null;
var analyticsChart = null;

function formatToday() {
    var d = new Date();
    var options = { year: 'numeric', month: 'short', day: 'numeric' };
    return d.toLocaleDateString('en-US', options);
}

function updateGreeting() {
    var hour = new Date().getHours();
    var timeOfDay = 'day';
    if (hour < 12) timeOfDay = 'morning';
    else if (hour < 18) timeOfDay = 'afternoon';
    else timeOfDay = 'evening';

    var name = (typeof librarianName !== 'undefined') ? librarianName : 'Librarian';
    document.getElementById('greetingText').textContent = 'Good ' + timeOfDay + ', ' + name + '!';
}

function setPeriod(period, el) {
    document.querySelectorAll('.period-tabs span').forEach(function (s) { s.classList.remove('active'); });
    el.classList.add('active');
}

function renderDashboard() {
    var todayStr = formatToday();
    document.getElementById('dashDateBooks').textContent = todayStr;
    document.getElementById('dashDateIssued').textContent = todayStr;

    var issued = 0, returned = 0;
    borrowers.forEach(function (p) {
        p.loans.forEach(function (l) {
            if (l.status === 'borrowed') issued++;
            else returned++;
        });
    });
    var pending = Math.max(0, Math.round(issued * 0.3));
    var total = issued + returned + pending;

    document.getElementById('donutCenterTotal').textContent = total;
    document.getElementById('statNewBooks').textContent = books.length;
    document.getElementById('statNewMembers').textContent = borrowers.length;
    document.getElementById('statReports').textContent = borrowers.length + books.length;

    var donutCtx = document.getElementById('booksDonutChart').getContext('2d');
    var donutData = {
        labels: ['Issued', 'Returned', 'Pending'],
        datasets: [{
            data: [issued, returned, pending],
            backgroundColor: ['#2e4b9b', '#e8a13a', '#2ea36c'],
            borderWidth: 0
        }]
    };
    if (donutChart) {
        donutChart.data = donutData;
        donutChart.update();
    } else {
        donutChart = new Chart(donutCtx, {
            type: 'doughnut',
            data: donutData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '72%',
                plugins: { legend: { display: false }, tooltip: { enabled: true } }
            }
        });
    }

    var sortedBooks = books.slice().sort(function (a, b) { return b.quantity - a.quantity; }).slice(0, 6);
    var barColors = sortedBooks.map(function (b, i) { return i === 0 ? '#2e4b9b' : '#c7d2ee'; });

    var mostIssuedCtx = document.getElementById('mostIssuedChart').getContext('2d');
    var mostIssuedData = {
        labels: sortedBooks.map(function (b) { return b.title.length > 12 ? b.title.slice(0, 12) + '…' : b.title; }),
        datasets: [{
            data: sortedBooks.map(function (b) { return b.quantity; }),
            backgroundColor: barColors,
            borderRadius: 6,
            maxBarThickness: 26
        }]
    };
    if (mostIssuedChart) {
        mostIssuedChart.data = mostIssuedData;
        mostIssuedChart.update();
    } else {
        mostIssuedChart = new Chart(mostIssuedCtx, {
            type: 'bar',
            data: mostIssuedData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true, ticks: { stepSize: 1 } },
                    x: { grid: { display: false } }
                }
            }
        });
    }

    var analyticsCtx = document.getElementById('analyticsChart').getContext('2d');
    var analyticsData = {
        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        datasets: [{
            label: 'Issues',
            data: [120, 190, 230, 180, 260, 210, 150],
            borderColor: '#2e4b9b',
            backgroundColor: 'rgba(46,75,155,.12)',
            fill: true,
            tension: 0.4,
            pointRadius: 3
        }]
    };
    if (analyticsChart) {
        analyticsChart.data = analyticsData;
        analyticsChart.update();
    } else {
        analyticsChart = new Chart(analyticsCtx, {
            type: 'line',
            data: analyticsData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    y: { beginAtZero: true },
                    x: { grid: { display: false } }
                }
            }
        });
    }
}

function logoutLibrarian() {
    if (confirm('Log out of the librarian dashboard?')) {
        // Hits LogoutServlet, which calls session.invalidate() server-side
        // before redirecting to login.jsp - a plain client redirect here
        // would leave the old session (and its role) still valid.
        window.location.href = '../logout';
    }
}

document.addEventListener('DOMContentLoaded', function () {
    updateGreeting();
    fetchTransactionBooks().then(function () {
        renderBooks();
        fetchBorrowers();
    });
});