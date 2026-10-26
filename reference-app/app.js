const cfg = window.SANDBOX_CONFIG;

const keycloak = new Keycloak({
  url: cfg.keycloakUrl,
  realm: cfg.realm,
  clientId: cfg.clientId
});

// --- DOM helpers ---------------------------------------------------------
const $ = (sel) => document.querySelector(sel);
const show = (el) => el.classList.remove("hidden");
const hide = (el) => el.classList.add("hidden");

// --- API helper: always send the bearer token + sandbox API key ----------
async function api(path, options = {}) {
  // Refresh the token if it is close to expiry; fall back to a fresh login.
  await keycloak.updateToken(30).catch(() => keycloak.login());
  const headers = Object.assign(
    {
      Authorization: "Bearer " + keycloak.token,
      [cfg.apiKeyHeader]: cfg.apiKey,
      "Content-Type": "application/json"
    },
    options.headers || {}
  );
  const res = await fetch(cfg.gatewayBaseUrl + path, { ...options, headers });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`${res.status} ${res.statusText} - ${text}`);
  }
  return res.status === 204 ? null : res.json();
}

// --- Rendering -----------------------------------------------------------
async function loadAccounts() {
  const accounts = await api("/accounts");
  const tbody = $("#accounts-table tbody");
  tbody.innerHTML = "";
  accounts.forEach((a) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${a.accountNumber}</td>
      <td>${a.accountType}</td>
      <td>${a.balance}</td>
      <td>${a.currency}</td>
      <td><button data-acc="${a.accountNumber}">Statement</button></td>`;
    tbody.appendChild(tr);
  });
  tbody.querySelectorAll("button[data-acc]").forEach((btn) =>
    btn.addEventListener("click", () => loadStatement(btn.dataset.acc))
  );
  if (accounts[0]) loadStatement(accounts[0].accountNumber);
}

async function loadStatement(accountNumber) {
  $("#statement-account").textContent = "(" + accountNumber + ")";
  const entries = await api("/transactions/accounts/" + accountNumber);
  const tbody = $("#txn-table tbody");
  tbody.innerHTML = "";
  entries.forEach((e) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${new Date(e.bookedAt).toLocaleString()}</td>
      <td>${e.direction}</td>
      <td>${e.amount}</td>
      <td>${e.description || ""}</td>`;
    tbody.appendChild(tr);
  });
}

async function doTransfer(evt) {
  evt.preventDefault();
  const form = evt.target;
  const body = {
    fromAccount: form.fromAccount.value,
    toAccount: form.toAccount.value,
    amount: parseFloat(form.amount.value),
    description: form.description.value
  };
  const out = $("#transfer-result");
  try {
    const result = await api("/payments/transfers", {
      method: "POST",
      body: JSON.stringify(body)
    });
    out.className = "result ok";
    out.textContent = `Transfer ${result.status} (ref ${result.transactionRef})`;
    await loadAccounts();
  } catch (err) {
    // Surface AML holds/blocks and insufficient-funds as readable messages.
    out.className = "result error";
    out.textContent = err.message;
  }
}

// --- Bootstrap -----------------------------------------------------------
function wireLoggedIn() {
  hide($("#login-view"));
  show($("#app-view"));
  show($("#user-bar"));
  $("#who").textContent = keycloak.tokenParsed?.preferred_username || "signed in";
  $("#logout-btn").addEventListener("click", () => keycloak.logout());
  $("#transfer-form").addEventListener("submit", doTransfer);
  loadAccounts().catch((e) => alert(e.message));
}

keycloak
  .init({ onLoad: "check-sso", pkceMethod: "S256" })
  .then((authenticated) => {
    if (authenticated) {
      wireLoggedIn();
    } else {
      $("#login-btn").addEventListener("click", () => keycloak.login());
    }
  })
  .catch((e) => alert("Keycloak init failed: " + e));
