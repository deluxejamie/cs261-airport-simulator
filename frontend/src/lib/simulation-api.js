const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options,
  });

  let body = null;
  try {
    body = await response.json();
  } catch {
    body = null;
  }

  if (!response.ok) {
    const message = body?.message || `Request failed (${response.status})`;
    throw new Error(message);
  }

  return body;
}

export async function createSimulation(config) {
  return request("/simulation/request", {
    method: "POST",
    body: JSON.stringify(config),
  });
}

export async function getSimulationStatus(uuid) {
  return request(`/simulation/status/${uuid}`);
}

export async function getSimulationResult(uuid) {
  return request(`/simulation/result/${uuid}`);
}

export async function getSimulationEventLog(uuid, offset = 0, count = 50) {
  return request(`/simulation/eventlog/${uuid}/${offset}/${count}`);
}