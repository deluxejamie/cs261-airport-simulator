"use client";
/**
 * Note that requests are made directly from the browser (not via the next server) for benefits discussed in design document
 * including easier ratelimiting, etc.
 */
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "";

/**
 * Utility function used to make requests to the backend server
 * @param {String} path The path of the endpoint (for example /test)
 * @param {{headers:object, ...requestInit}} options A headers object and options from the [fetch api](https://developer.mozilla.org/en-US/docs/Web/API/Window/fetch#options)
 * @returns
 */
async function request(path, options = {}) {
	try {
		const response = await fetch(`${API_BASE}${path}`, {
			headers: {
				"Content-Type": "application/json",
				...(options.headers || {}),
			},
			cache: "no-cache",
			...options,
		});
		// all responses will include a json body, so the fn should throw an error if not successful
		let body = await response.json();

		if (!response.ok) {
			const message = body?.message || `Request failed (${response.status})`;
			throw new Error(message);
		}
		return body;
	} catch (e) {
		throw e;
	}
}

/**
 * Informs the backend to start a simulation using the provided configuration file.
 * @param {String} config A JSON representing the configuration for the simulation to submit
 * @returns {String} Either the simulation id of the created simulation, or the string "request_failed"
 */
export async function createSimulation(config) {
	// Mocked response (uncomment for testing)
	// return "84f43a1c-9ec3-4139-885c-f929f3167cce";
	try {
		const res = await request("/simulation/request", {
			method: "POST",
			body: config,
		});
		return res.sim_id ?? "request_failed";
	} catch (e) {
		return "request_failed";
	}
}

/**
 * Gets the simulation status from an unsanitised source from the browser (unsanitised)
 * @param {String} uuid The uuid of the simulation to check the status of
 * @returns {String} Either the status "in_progress", "complete", or "unavailable" if the api is unreachable
 */
export async function getSimulationStatus(uuid) {
	const safeID = encodeURIComponent(uuid);
	try {
		const res = await request(`/simulation/status/${safeID}`);
		return res.status ?? "unavailable";
	} catch (e) {
		return "unavailable";
	}
}

export async function getSimulationResult(uuid) {
	const safeID = encodeURIComponent(uuid);
	return await request(`/simulation/result/${safeID}`);
}

export async function getSimulationEventLog(uuid, offset = 0, count = 50) {
	return await request(
		`/simulation/eventlog/` +
			encodeURIComponent(uuid) +
			"/" +
			encodeURIComponent(offset) +
			"/" +
			encodeURIComponent(count),
	);
}
