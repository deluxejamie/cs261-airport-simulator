"use client";
import { EmergencyStatus } from "@/app/hooks";
import { sleep } from "@/components/simulation-outcome-page";
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
	if (uuid == "84f43a1c-9ec3-4139-885c-f929f3167cce") {
		await sleep(300);
		return "complete";
	}
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

export const EventTypes = {
	LANDING: "landing_event",
	TAKEOFF: "takeoff_event",
	HOLDING: "holding_event",
	DIVERSION: "diversion_event",
	CANCELLATION: "cancellation_event",
	EMERGENCY: "emergency_event",
	RUNWAY_MODE: "runway_mode_event",
	RUNWAY_STATUS: "runway_status_event",
};
/**
 * Gets event log entries for a provided simulation
 * @param {String} uuid The uuid of the simulation to get entries for
 * @param {Number} offset The offset to gather from
 * @param {Number} count The count of events to get
 * @returns {success: boolean, events?: Event[], total_events?: Number} (expected from server) The response from the server. Other properties only populated if success is true
 */
export async function getSimulationEventLog(uuid, offset = 0, count = 50) {
	if (uuid == "84f43a1c-9ec3-4139-885c-f929f3167cce") {
		const mockedEvents = [
			{
				id: 1,
				eventType: EventTypes.LANDING,
				simulationId: uuid,
				simTimestamp: 0,
				attributes: {
					callsign: "BA-1",
					holdMinutes: 3,
					runwayNumber: 2,
					arrivalDelay: 4,
				},
			},
			{
				id: 2,
				eventType: EventTypes.TAKEOFF,
				simulationId: uuid,
				simTimestamp: 10,
				attributes: {
					callsign: "BA-2",
					runwayNumber: 2,
					waitMinutes: 3,
					departureDelay: 5,
				},
			},
			{
				id: 3,
				eventType: EventTypes.HOLDING,
				simulationId: uuid,
				simTimestamp: 50,
				attributes: {
					callsign: "EASYJET-3",
					op: "not sure what this is",
				},
			},
			{
				id: 4,
				eventType: EventTypes.DIVERSION,
				simulationId: uuid,
				simTimestamp: 50,
				attributes: {
					callsign: "EASYJET-4",
					reason: "Flight has low fuel.",
				},
			},
			{
				id: 5,
				eventType: EventTypes.CANCELLATION,
				simulationId: uuid,
				simTimestamp: 55,
				attributes: {
					callsign: "EASYJET-5",
					reason: `No takeoff capacity after waiting 20 minutes`,
					waitingMinutes: 20,
				},
			},
			{
				id: 6,
				eventType: EventTypes.EMERGENCY,
				simulationId: uuid,
				simTimestamp: 65,
				attributes: {
					callsign: "QATAR-6",
					emergencyStatus: "fuel",
				},
			},
			{
				id: 7,
				eventType: EventTypes.RUNWAY_MODE,
				simulationId: uuid,
				simTimestamp: 80,
				attributes: {
					callsign: "QATAR-6",
					emergencyStatus: "fuel",
				},
			},
			{
				id: 8,
				eventType: EventTypes.RUNWAY_STATUS,
				simulationId: uuid,
				simTimestamp: 80,
				attributes: {
					callsign: "QATAR-6",
					status: "not sure what this is",
				},
			},
		];
		await sleep(300); // mock the time taken for a request, 300ms
		return {
			events: mockedEvents.slice(offset, offset + count),
			total_events: mockedEvents.length,
			success: true,
		};
	}
	try {
		const res = await request(
			`/simulation/eventlog/` +
				encodeURIComponent(uuid) +
				"/" +
				encodeURIComponent(offset) +
				"/" +
				encodeURIComponent(count),
		);
		return { ...res, success: true };
	} catch (e) {
		return { success: false };
	}
}
