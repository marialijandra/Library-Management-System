<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Manager - iACADEMY</title>
    <link rel="icon" type="image/png" href="../images/logofooter.png">
    <link rel="stylesheet" href="../css/librarian.css">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,600,700&display=swap" rel="stylesheet">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.0/chart.umd.min.js"></script>
</head>
<body>

<div class="dash-wrapper">

    <aside class="sidebar">
        <div class="side-logo">
            <img src="../images/logofooter.png" alt="iACADEMY">
            <span>iACADEMY Library</span>
        </div>
        <ul class="side-nav">
            <li><a href="#" id="nav-dashboard" class="active" onclick="switchTab('dashboard'); return false;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="9" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="16" width="7" height="5" rx="1"/></svg>
                <span>Dashboard</span>
            </a></li>
            <li><a href="#" id="nav-books" onclick="switchTab('books'); return false;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                <span>Books</span>
            </a></li>
            <li><a href="#" id="nav-transaction" onclick="switchTab('transaction'); return false;">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 1l4 4-4 4"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><path d="M7 23l-4-4 4-4"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>
                <span>Transaction</span>
            </a></li>
        </ul>
        <div class="side-logout-wrap">
            <button class="side-logout" onclick="logoutLibrarian()">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                <span>Logout</span>
            </button>
        </div>
    </aside>

    <div class="main-area">

        <div class="content no-scroll" id="mainContent">

            <div class="tab-view active" id="tab-dashboard">

                <div class="dash-welcome-row">
                    <div class="dash-welcome">
                        <div class="avatar-lg">M</div>
                        <div>
                            <h1 id="greetingText">Good day, Maria!</h1>
                            <p>Library Manager</p>
                        </div>
                    </div>
                    <div class="period-tabs">
                        <span class="active" data-period="today" onclick="setPeriod('today', this)">Today</span>
                        <span data-period="weekly" onclick="setPeriod('weekly', this)">Weekly</span>
                        <span data-period="monthly" onclick="setPeriod('monthly', this)">Monthly</span>
                        <span data-period="yearly" onclick="setPeriod('yearly', this)">Yearly</span>
                    </div>
                </div>

                <div class="dash-top-row">
                    <div class="panel">
                        <div class="panel-header">
                            <h2>Books</h2>
                            <span class="panel-date" id="dashDateBooks"></span>
                        </div>
                        <div class="donut-row">
                            <div class="donut-chart-wrap">
                                <canvas id="booksDonutChart"></canvas>
                                <div class="donut-center" id="donutCenterTotal">0</div>
                            </div>
                            <div class="donut-legend">
                                <div class="legend-item"><span class="legend-dot issued"></span> Issued</div>
                                <div class="legend-item"><span class="legend-dot returned"></span> Returned</div>
                                <div class="legend-item"><span class="legend-dot pending"></span> Pending</div>
                            </div>
                        </div>
                        <a class="see-list-link" onclick="switchTab('books')">See list</a>
                    </div>

                    <div class="panel">
                        <div class="panel-header">
                            <h2>Most Issued</h2>
                            <span class="panel-date" id="dashDateIssued"></span>
                        </div>
                        <div class="most-issued-chart">
                            <canvas id="mostIssuedChart"></canvas>
                        </div>
                    </div>
                </div>

                <div class="dash-bottom-row">
                    <div class="panel">
                        <div class="panel-header">
                            <h2>Analytics</h2>
                            <span class="panel-date">This Week</span>
                        </div>
                        <div class="analytics-chart">
                            <canvas id="analyticsChart"></canvas>
                        </div>
                    </div>

                    <div class="dash-stat-cards">
                        <div class="dash-stat-card card-blue">
                            <div class="stat-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                            </div>
                            <div class="stat-text">
                                <div class="label">New books</div>
                                <div class="value" id="statNewBooks">0</div>
                            </div>
                        </div>
                        <div class="dash-stat-card card-gold">
                            <div class="stat-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                            </div>
                            <div class="stat-text">
                                <div class="label">New members</div>
                                <div class="value" id="statNewMembers">0</div>
                            </div>
                        </div>
                        <div class="dash-stat-card card-green">
                            <div class="stat-icon">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                            </div>
                            <div class="stat-text">
                                <div class="label">Reports</div>
                                <div class="value" id="statReports">0</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="tab-view" id="tab-books">
                <div class="panel">
                    <div class="panel-header">
                        <h2>Book Catalog</h2>
                        <button class="btn-primary" onclick="openAddBookModal()">+ Add Book</button>
                    </div>

                    <div class="search-bar">
                        <input type="text" id="bookSearchInput" placeholder="Search books..." oninput="searchBooks()">
                    </div>

                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Quantity</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="booksTableBody"></tbody>
                    </table>
                </div>
            </div>

            <div class="tab-view" id="tab-transaction">
                <div class="panel">
                    <div class="panel-header">
                        <h2>Borrowers</h2>
                        <button class="btn-primary" onclick="openAddBorrowerModal()">+ Add New Borrower</button>
                    </div>

                    <div class="search-bar">
                        <input type="text" id="borrowerSearchInput" placeholder="Search borrowers..." oninput="searchBorrowers()">
                    </div>

                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Books Borrowed</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody id="borrowersTableBody"></tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>

<div class="modal-overlay" id="editBookModal">
    <div class="modal-box">
        <div class="modal-head">
            <h3>Edit Book</h3>
            <button class="modal-close" onclick="closeEditBookModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div class="mf-group">
                <label>Title</label>
                <input type="text" id="editBookTitle">
            </div>
            <div class="mf-group">
                <label>Description</label>
                <textarea id="editBookDescription" placeholder="Short two-sentence description"></textarea>
            </div>
            <div class="mf-group">
                <label>Quantity</label>
                <input type="number" id="editBookQuantity" min="0">
            </div>
            <div class="mf-group">
                <label>Image URL</label>
                <input type="text" id="editBookImage" placeholder="https://...">
            </div>
        </div>
        <div class="modal-foot">
            <button class="btn-danger" onclick="deleteBookFromModal()">Delete</button>
            <button class="btn-primary" onclick="saveEditedBook()">Save</button>
        </div>
    </div>
</div>

<div class="modal-overlay" id="addBookModal">
    <div class="modal-box">
        <div class="modal-head">
            <h3>Add Book</h3>
            <button class="modal-close" onclick="closeAddBookModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div id="addBookEntries"></div>
            <button type="button" class="add-entry-btn" onclick="addBookEntryBlock()">+ Add Another Book</button>
        </div>
        <div class="modal-foot">
            <button class="btn-primary" onclick="saveNewBooks()">Save</button>
        </div>
    </div>
</div>

<div class="modal-overlay" id="borrowerModal">
    <div class="modal-box">
        <div class="modal-head">
            <h3 id="borrowerModalName">Borrower</h3>
            <button class="modal-close" onclick="closeBorrowerModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div id="borrowerLoanRows"></div>
            <div id="borrowerNewBookRows" style="margin-top: 14px; padding-top: 14px; border-top: 1px solid #eef0f7;"></div>
            <button type="button" class="add-entry-btn" onclick="addBorrowerLoanBookRow()">+ Add Book</button>
        </div>
        <div class="modal-foot">
            <button class="btn-primary" onclick="saveBorrowerStatuses()">Save</button>
        </div>
    </div>
</div>

<div class="modal-overlay" id="addBorrowerModal">
    <div class="modal-box">
        <div class="modal-head">
            <h3>Add New Borrower</h3>
            <button class="modal-close" onclick="closeAddBorrowerModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div class="mf-group">
                <label>Book</label>
                <div id="newBorrowerBookRows"></div>
                <button type="button" class="add-entry-btn" onclick="addBorrowerBookRow()">+ Add Another Book</button>
            </div>
            <div class="mf-group">
                <label>First Name</label>
                <input type="text" id="newBorrowerFirstName" placeholder="First Name">
            </div>
            <div class="mf-group">
                <label>Surname</label>
                <input type="text" id="newBorrowerSurname" placeholder="Surname">
            </div>
            <div class="mf-group">
                <label>Email</label>
                <input type="email" id="newBorrowerEmail" placeholder="student@iacademy.edu.ph">
            </div>
        </div>
        <div class="modal-foot">
            <button class="btn-primary" onclick="saveNewBorrower()">Save</button>
        </div>
    </div>
</div>

<script src="../js/librarian.js"></script>
</body>
</html>
