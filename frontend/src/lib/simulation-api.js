"use client";
/**
 * Note that requests are made directly from the browser (not via the next server) for benefits discussed in design document
 * including easier ratelimiting, etc.
 */
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "";

function request(path, options = {}) {
	let success = true;
	const response = fetch(`${API_BASE}${path}`, {
		headers: { "Content-Type": "application/json", ...(options.headers || {}) },
		...options,
	}).catch(() => {
		success = false;
	});
	if (!success) throw new Error("Request failed (unable to fetch)");

	let body = null;
	try {
		body = response.json();
	} catch {
		body = null;
	}

	if (!response.ok) {
		const message = body?.message || `Request failed (${response.status})`;
		throw new Error(message);
	}

	return body;
}

export function createSimulation(config) {
	// Mocked response:
	//return "84f43a1c-9ec3-4139-885c-f929f3167cce";
	try {
		const res = request("/simulation/request", {
			method: "POST",
			body: config,
		});
		return res.sim_id ?? "request_failed";
	} catch (e) {
		return "request_failed";
	}
}

export function getSimulationStatus(uuid) {
	return request(`/simulation/status/${uuid}`);
}

export function getSimulationResult(uuid) {
	return request(`/simulation/result/${uuid}`);
}

export function getSimulationEventLog(uuid, offset = 0, count = 50) {
	return request(`/simulation/eventlog/${uuid}/${offset}/${count}`);
}
