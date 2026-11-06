// ===== STATE =====
let state = {
    currentScreen: 'login',
    currentGameId: null,
    pollingInterval: null
};

// ===== DOM REFS =====
const $ = id => document.getElementById(id);

const screens = {
    login: $('screen-login'),
    menu: $('screen-menu'),
    available: $('screen-available'),
    history: $('screen-history'),
    leaderboard: $('screen-leaderboard'),
    game: $('screen-game')
};

// ===== AUTH (JWT) =====
function getAuth() {
    const data = sessionStorage.getItem('auth');
    return data ? JSON.parse(data) : null;
}

function setAuth(accessToken, refreshToken, login) {
    sessionStorage.setItem('auth', JSON.stringify({ accessToken, refreshToken, login }));
}

function clearAuth() {
    sessionStorage.removeItem('auth');
}

function getAuthHeader() {
    const auth = getAuth();
    return auth ? { 'Authorization': 'Bearer ' + auth.accessToken } : {};
}

// ===== API =====
async function api(method, url, body = null, customHeaders = {}) {
    const headers = { ...getAuthHeader(), ...customHeaders };
    if (body) {
        headers['Content-Type'] = 'application/json';
    }

    const res = await fetch(url, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null
    });

    let data = null;
    const contentType = res.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        data = await res.json();
    } else {
        data = await res.text();
    }

    if (!res.ok) {
        // Если 401 — попробуем обновить accessToken
        if (res.status === 401 && !url.includes('/auth/')) {
            const refreshed = await tryRefreshToken();
            if (refreshed) {
                // Повторяем исходный запрос с новым токеном
                const headers2 = { 'Authorization': 'Bearer ' + getAuth().accessToken, ...customHeaders };
                if (body) headers2['Content-Type'] = 'application/json';
                const res2 = await fetch(url, {
                    method,
                    headers: headers2,
                    body: body ? JSON.stringify(body) : null
                });
                const contentType2 = res2.headers.get('content-type') || '';
                let data2 = null;
                if (contentType2.includes('application/json')) {
                    data2 = await res2.json();
                } else {
                    data2 = await res2.text();
                }
                if (!res2.ok) {
                    const errMsg2 = data2 && data2.error ? data2.error + ' (' + res2.status + ')' : 'Ошибка ' + res2.status;
                    throw { message: errMsg2, status: res2.status, data: data2 };
                }
                return data2;
            } else {
                // Не удалось обновить — разлогиниваем
                clearAuth();
                showScreen('login');
                throw { message: 'Сессия истекла, войдите заново', status: 401, data: null };
            }
        }
        const errMsg = data && data.error ? data.error + ' (' + res.status + ')' : 'Ошибка ' + res.status;
        throw { message: errMsg, status: res.status, data };
    }

    return data;
}

// Попытка обновить accessToken через refreshToken
async function tryRefreshToken() {
    const auth = getAuth();
    if (!auth || !auth.refreshToken) return false;

    try {
        const res = await fetch('/auth/refresh-access', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: auth.refreshToken })
        });
        if (!res.ok) return false;
        const data = await res.json();
        // data = { type, accessToken, refreshToken } — refreshToken остаётся тем же
        auth.accessToken = data.accessToken;
        sessionStorage.setItem('auth', JSON.stringify(auth));
        return true;
    } catch (e) {
        return false;
    }
}

// ===== SCREENS =====
function showScreen(name) {
    Object.keys(screens).forEach(key => {
        screens[key].classList.remove('active');
    });
    screens[name].classList.add('active');
    state.currentScreen = name;
    document.querySelectorAll('.error-message').forEach(el => {
        el.classList.add('hidden');
        el.textContent = '';
    });
}

function showError(screenName, msg) {
    const el = $(screenName + '-error');
    if (el) {
        el.textContent = msg;
        el.classList.remove('hidden');
    }
}

// ===== LOGIN SCREEN =====
$('btn-login').addEventListener('click', handleLogin);
$('btn-register').addEventListener('click', handleRegister);
$('login-input').addEventListener('keydown', e => { if (e.key === 'Enter') handleLogin(); });
$('password-input').addEventListener('keydown', e => { if (e.key === 'Enter') handleLogin(); });

async function handleLogin() {
    const login = $('login-input').value.trim();
    const password = $('password-input').value.trim();

    if (!login || !password) {
        showError('login', 'Заполните логин и пароль');
        return;
    }

    try {
        const data = await api('POST', '/auth/login', { login, password });
        // data = { type, accessToken, refreshToken }
        // login берём из поля ввода, т.к. бэкенд не возвращает login в ответе
        setAuth(data.accessToken, data.refreshToken, login);
        enterMenu();
    } catch (err) {
        showError('login', 'Ошибка входа: ' + err.message);
    }
}

async function handleRegister() {
    const login = $('login-input').value.trim();
    const password = $('password-input').value.trim();

    if (!login || !password) {
        showError('login', 'Заполните логин и пароль');
        return;
    }

    try {
        await api('POST', '/auth/register', { login, password });
        // После регистрации сразу логинимся
        const data = await api('POST', '/auth/login', { login, password });
        // login берём из поля ввода, т.к. бэкенд не возвращает login в ответе
        setAuth(data.accessToken, data.refreshToken, login);
        enterMenu();
    } catch (err) {
        if (err.status === 409) {
            showError('login', 'Пользователь с таким логином уже существует');
        } else {
            showError('login', 'Ошибка регистрации: ' + err.message);
        }
    }
}

// ===== MENU SCREEN =====
function enterMenu() {
    const auth = getAuth();
    if (!auth) {
        showScreen('login');
        return;
    }
    $('greeting').textContent = 'Привет, ' + auth.login + '!';
    showScreen('menu');
    checkContinueButton();
}

function checkContinueButton() {
    const savedGameId = sessionStorage.getItem('currentGameId');
    const btn = $('btn-continue');
    if (savedGameId) {
        btn.classList.remove('hidden');
        btn.textContent = 'Продолжить игру';
    } else {
        btn.classList.add('hidden');
    }
}

$('btn-new-ai').addEventListener('click', () => createGame('VS_AI'));
$('btn-new-pvp').addEventListener('click', () => createGame('VS_PLAYER'));

async function createGame(type) {
    try {
        const game = await api('POST', '/game/create?type=' + type);
        state.currentGameId = game.id;
        sessionStorage.setItem('currentGameId', game.id);
        enterGame(game);
    } catch (err) {
        showError('menu', 'Ошибка создания игры: ' + err.message);
    }
}

$('btn-available').addEventListener('click', async () => {
    try {
        const games = await api('GET', '/game/available');
        showAvailableGames(games);
    } catch (err) {
        showError('menu', 'Ошибка загрузки игр: ' + err.message);
    }
});

$('btn-history').addEventListener('click', async () => {
    try {
        const games = await api('GET', '/game/history');
        showHistory(games);
    } catch (err) {
        showError('menu', 'Ошибка загрузки истории: ' + err.message);
    }
});

$('btn-leaderboard').addEventListener('click', async () => {
    try {
        const top = await api('GET', '/game/top?n=10');
        showLeaderboard(top);
    } catch (err) {
        showError('menu', 'Ошибка загрузки лидеров: ' + err.message);
    }
});

$('btn-continue').addEventListener('click', async () => {
    const gameId = sessionStorage.getItem('currentGameId');
    if (!gameId) return;
    try {
        const game = await api('GET', '/game/' + gameId);
        state.currentGameId = gameId;
        enterGame(game);
    } catch (err) {
        showError('menu', 'Ошибка загрузки игры: ' + err.message);
    }
});

$('btn-logout').addEventListener('click', () => {
    clearAuth();
    sessionStorage.removeItem('currentGameId');
    if (state.pollingInterval) {
        clearInterval(state.pollingInterval);
        state.pollingInterval = null;
    }
    showScreen('login');
});

// ===== AVAILABLE GAMES SCREEN =====
function showAvailableGames(games) {
    showScreen('available');
    const container = $('available-list');
    container.innerHTML = '';

    if (!games || games.length === 0) {
        container.innerHTML = '<div class="empty-list">Нет доступных игр</div>';
        return;
    }

    games.forEach(game => {
        const item = document.createElement('div');
        item.className = 'available-item';

        const info = document.createElement('span');
        info.className = 'game-info';
        const shortId = game.id ? game.id.substring(0, 8) + '...' : '???';
        info.textContent = 'Игра #' + shortId;

        const joinBtn = document.createElement('button');
        joinBtn.className = 'btn btn-primary';
        joinBtn.textContent = 'Присоединиться';
        joinBtn.addEventListener('click', () => joinGame(game.id));

        item.appendChild(info);
        item.appendChild(joinBtn);
        container.appendChild(item);
    });
}

async function joinGame(gameId) {
    try {
        const game = await api('POST', '/game/' + gameId + '/join');
        state.currentGameId = game.id;
        sessionStorage.setItem('currentGameId', game.id);
        enterGame(game);
    } catch (err) {
        showError('available', 'Ошибка присоединения: ' + err.message);
    }
}

$('btn-back-menu-from-available').addEventListener('click', () => enterMenu());

// ===== HISTORY SCREEN =====
function showHistory(games) {
    showScreen('history');
    const container = $('history-list');
    container.innerHTML = '';

    if (!games || games.length === 0) {
        container.innerHTML = '<div class="empty-list">История игр пуста</div>';
        return;
    }

    games.forEach(game => {
        const item = document.createElement('div');
        item.className = 'history-item';

        const statusText = getStatusText(game.status);
        const typeText = game.gameType === 'VS_AI' ? 'против компьютера' : 'против игрока';

        item.innerHTML = `
            <div class="history-info">
                <span class="history-status ${getStatusClass(game.status)}">${statusText}</span>
                <span class="history-type">${typeText}</span>
            </div>
        `;

        container.appendChild(item);
    });
}

function getStatusText(status) {
    switch (status) {
        case 'PLAYER_X_WIN': return 'Победа X';
        case 'PLAYER_O_WIN': return 'Победа O';
        case 'DRAW': return 'Ничья';
        default: return status;
    }
}

function getStatusClass(status) {
    switch (status) {
        case 'PLAYER_X_WIN': return 'status-win-x';
        case 'PLAYER_O_WIN': return 'status-win-o';
        case 'DRAW': return 'status-draw';
        default: return '';
    }
}

$('btn-back-menu-from-history').addEventListener('click', () => enterMenu());

// ===== LEADERBOARD SCREEN =====
function showLeaderboard(entries) {
    showScreen('leaderboard');
    const container = $('leaderboard-list');
    container.innerHTML = '';

    if (!entries || entries.length === 0) {
        container.innerHTML = '<div class="empty-list">Нет данных для таблицы лидеров</div>';
        return;
    }

    const table = document.createElement('table');
    table.className = 'leaderboard-table';

    const thead = document.createElement('thead');
    thead.innerHTML = `
        <tr>
            <th>#</th>
            <th>Игрок</th>
            <th>Соотношение побед</th>
        </tr>
    `;
    table.appendChild(thead);

    const tbody = document.createElement('tbody');
    entries.forEach((entry, index) => {
        const tr = document.createElement('tr');
        // winRatio может быть в поле winRatio или ratio
        const ratio = entry.winRatio !== undefined ? entry.winRatio : (entry.ratio || 0);
        const winRatio = (ratio * 100).toFixed(1) + '%';
        tr.innerHTML = `
            <td>${index + 1}</td>
            <td>${entry.login || entry.userId}</td>
            <td>${winRatio}</td>
        `;
        tbody.appendChild(tr);
    });
    table.appendChild(tbody);

    container.appendChild(table);
}

$('btn-back-menu-from-leaderboard').addEventListener('click', () => enterMenu());

// ===== GAME SCREEN =====
function enterGame(game) {
    showScreen('game');
    renderBoard(game);
}

function renderBoard(game) {
    const boardEl = $('board');
    boardEl.innerHTML = '';

    // Проверяем, что поле корректное (3x3 массив)
    const field = (game.field && Array.isArray(game.field) && game.field.length === 3)
        ? game.field
        : [[0,0,0],[0,0,0],[0,0,0]];
    const status = game.status;
    const isGameOver = (status === 'PLAYER_X_WIN' || status === 'PLAYER_O_WIN' || status === 'DRAW');

    const typeLabel = game.gameType === 'VS_AI' ? 'против компьютера' : 'против игрока';
    $('game-title').textContent = 'Игра ' + typeLabel;

    const statusEl = $('game-status');
    if (isGameOver) {
        let msg = '';
        if (status === 'PLAYER_X_WIN') msg = 'Победили X!';
        else if (status === 'PLAYER_O_WIN') msg = 'Победили O!';
        else msg = 'Ничья!';
        statusEl.textContent = msg;
    } else if (status === 'WAITING_PLAYERS') {
        statusEl.textContent = 'Ожидание второго игрока...';
    } else if (status === 'PLAYER_X_TURN') {
        statusEl.textContent = 'Ход X';
    } else if (status === 'PLAYER_O_TURN') {
        statusEl.textContent = 'Ход O';
    }

    const resultEl = $('game-result');
    if (isGameOver) {
        resultEl.classList.remove('hidden');
        if (status === 'PLAYER_X_WIN') {
            resultEl.className = 'game-result win-x';
            resultEl.textContent = 'Победа X!';
        } else if (status === 'PLAYER_O_WIN') {
            resultEl.className = 'game-result win-o';
            resultEl.textContent = 'Победа O!';
        } else {
            resultEl.className = 'game-result draw';
            resultEl.textContent = 'Ничья!';
        }
    } else {
        resultEl.classList.add('hidden');
    }

    for (let i = 0; i < 3; i++) {
        for (let j = 0; j < 3; j++) {
            const cell = document.createElement('div');
            cell.className = 'cell';
            cell.dataset.row = i;
            cell.dataset.col = j;

            const val = field[i] ? field[i][j] : 0;
            if (val === 1) {
                cell.textContent = 'X';
                cell.classList.add('cell-taken', 'cell-x');
            } else if (val === 2) {
                cell.textContent = 'O';
                cell.classList.add('cell-taken', 'cell-o');
            } else {
                if (isGameOver || status === 'WAITING_PLAYERS') {
                    cell.classList.add('cell-disabled');
                } else {
                    // Используем немедленно вызываемую функцию для захвата game.id
                    (function(gameId, i, j) {
                        cell.addEventListener('click', () => handleCellClick(gameId, i, j));
                    })(game.id, i, j);
                }
            }

            boardEl.appendChild(cell);
        }
    }

    // PvP: polling на всё время игры для синхронизации
    // AI: polling не нужен
    if (game.gameType === 'VS_PLAYER' && !isGameOver) {
        startPolling(game.id);
    } else {
        stopPolling();
    }
}

async function handleCellClick(gameId, row, col) {
    try {
        // Получаем свежее состояние игры с сервера
        const game = await api('GET', '/game/' + gameId);
        const status = game.status;
        const freshField = game.field;

        let val;
        if (status === 'PLAYER_X_TURN') val = 1;
        else if (status === 'PLAYER_O_TURN') val = 2;
        else return; // Не наш ход или игра окончена

        if (freshField[row][col] !== 0) return; // Клетка занята

        // Копируем свежую доску и ставим ход
        const newField = freshField.map(arr => [...arr]);
        newField[row][col] = val;

        // Отправляем ход на сервер. Бэкенд ожидает GameWebModel с полем field
        const updated = await api('POST', '/game/' + gameId, { field: newField, id: gameId });
        renderBoard(updated);
    } catch (err) {
        showError('game', 'Ошибка хода: ' + err.message);
        // Обновляем доску с сервера, чтобы синхронизироваться
        try {
            const fresh = await api('GET', '/game/' + gameId);
            renderBoard(fresh);
        } catch (e) {
            // ignore
        }
    }
}

// ===== POLLING for PvP =====
function startPolling(gameId) {
    stopPolling();
    state.pollingInterval = setInterval(async () => {
        try {
            const game = await api('GET', '/game/' + gameId);
            renderBoard(game);
        } catch (e) {
            // Если ошибка — пытаемся дальше, не останавливаем polling
        }
    }, 1500);
}

function stopPolling() {
    if (state.pollingInterval) {
        clearInterval(state.pollingInterval);
        state.pollingInterval = null;
    }
}

$('btn-back-menu-from-game').addEventListener('click', () => {
    stopPolling();
    sessionStorage.removeItem('currentGameId');
    state.currentGameId = null;
    enterMenu();
});

// ===== INIT =====
(function init() {
    const auth = getAuth();
    if (auth) {
        enterMenu();
    } else {
        showScreen('login');
    }
})();