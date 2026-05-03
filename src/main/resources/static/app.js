const state = {
  token: localStorage.getItem("token"),
  user: JSON.parse(localStorage.getItem("user") || "null"),
  selectedProjectId: null,
  selectedProjectMembers: [],
};

const authSection = document.getElementById("authSection");
const appSection = document.getElementById("appSection");
const flash = document.getElementById("flash");
const projectSelect = document.getElementById("projectSelect");
const projectDetailsBox = document.getElementById("projectDetailsBox");
const userSelect = document.getElementById("userSelect");
const assigneeSelect = document.getElementById("assigneeSelect");

function notify(message, isError = false) {
  flash.textContent = message;
  flash.style.color = isError ? "red" : "green";
}

async function api(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json" };
  if (state.token) headers.Authorization = `Bearer ${state.token}`;
  const res = await fetch(path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: "Request failed" }));
    throw new Error(err.message || "Request failed");
  }
  return res.status === 204 ? null : res.json();
}

function toggleUI() {
  const loggedIn = Boolean(state.token);
  authSection.classList.toggle("hidden", loggedIn);
  appSection.classList.toggle("hidden", !loggedIn);
  if (loggedIn) {
    document.getElementById("welcomeText").textContent = `Dashboard - ${state.user.name}`;
    bootstrapApp();
  }
}

async function bootstrapApp() {
  await Promise.all([loadSummary(), loadProjects(), loadUsers()]);
}

async function loadSummary() {
  const data = await api("/api/dashboard/summary");
  document.getElementById("summaryBox").textContent = JSON.stringify(data, null, 2);
}

async function loadProjects() {
  const projects = await api("/api/projects");
  projectSelect.innerHTML = "";
  projects.forEach((p) => {
    const opt = document.createElement("option");
    opt.value = p.id;
    opt.textContent = `${p.id} - ${p.name}`;
    projectSelect.appendChild(opt);
  });
  if (projects.length) {
    state.selectedProjectId = Number(projects[0].id);
    await loadProjectDetails();
  }
}

async function loadUsers() {
  const users = await api("/api/users");
  userSelect.innerHTML = "";
  users.forEach((u) => {
    const opt = document.createElement("option");
    opt.value = u.id;
    opt.textContent = `${u.name} (${u.email})`;
    userSelect.appendChild(opt);
  });
}

async function loadProjectDetails() {
  state.selectedProjectId = Number(projectSelect.value);
  const details = await api(`/api/projects/${state.selectedProjectId}`);
  state.selectedProjectMembers = details.members;
  projectDetailsBox.textContent = JSON.stringify(details, null, 2);
  assigneeSelect.innerHTML = "";
  details.members.forEach((m) => {
    const opt = document.createElement("option");
    opt.value = m.userId;
    opt.textContent = `${m.userName} (${m.role})`;
    assigneeSelect.appendChild(opt);
  });
}

async function loadTasks() {
  const tasks = await api(`/api/tasks/project/${state.selectedProjectId}`);
  document.getElementById("tasksBox").textContent = JSON.stringify(tasks, null, 2);
}

document.getElementById("signupForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const payload = {
      name: document.getElementById("signupName").value,
      email: document.getElementById("signupEmail").value,
      password: document.getElementById("signupPassword").value,
    };
    const data = await api("/auth/signup", "POST", payload);
    state.token = data.token;
    state.user = { id: data.userId, name: data.name, email: data.email };
    localStorage.setItem("token", state.token);
    localStorage.setItem("user", JSON.stringify(state.user));
    notify("Signup successful");
    toggleUI();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const payload = {
      email: document.getElementById("loginEmail").value,
      password: document.getElementById("loginPassword").value,
    };
    const data = await api("/auth/login", "POST", payload);
    state.token = data.token;
    state.user = { id: data.userId, name: data.name, email: data.email };
    localStorage.setItem("token", state.token);
    localStorage.setItem("user", JSON.stringify(state.user));
    notify("Login successful");
    toggleUI();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("projectForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    await api("/api/projects", "POST", {
      name: document.getElementById("projectName").value,
      description: document.getElementById("projectDescription").value,
    });
    notify("Project created");
    await loadProjects();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("memberForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    await api(`/api/projects/${state.selectedProjectId}/members`, "POST", {
      userId: Number(userSelect.value),
      role: document.getElementById("memberRole").value,
    });
    notify("Member added");
    await loadProjectDetails();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("taskForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    await api("/api/tasks", "POST", {
      projectId: state.selectedProjectId,
      title: document.getElementById("taskTitle").value,
      description: document.getElementById("taskDescription").value,
      assignedToUserId: Number(assigneeSelect.value),
      dueDate: document.getElementById("taskDueDate").value,
    });
    notify("Task created");
    await loadTasks();
    await loadSummary();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("loadProjectBtn").addEventListener("click", async () => {
  try {
    await loadProjectDetails();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("loadTasksBtn").addEventListener("click", async () => {
  try {
    await loadTasks();
  } catch (err) {
    notify(err.message, true);
  }
});

document.getElementById("logoutBtn").addEventListener("click", () => {
  state.token = null;
  state.user = null;
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  toggleUI();
});

projectSelect.addEventListener("change", async () => {
  try {
    await loadProjectDetails();
  } catch (err) {
    notify(err.message, true);
  }
});

toggleUI();
