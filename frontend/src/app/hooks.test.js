import { renderHook, act } from "@testing-library/react";
import { useRunways, RunwayModes, useAdvancedConfig, useFlightSchedule, FlightType, EmergencyStatus, useHazardSchedule, HazardType, RunwayClosureMode, EmergencyStatusWithoutNone } from "./hooks";

describe("useRunways hook", () => {
  it("should start empty", () => {
    const { result } = renderHook(() => useRunways());
    expect(result.current.runways).toEqual([]);
  });

  it("should add a runway", () => {
    const { result } = renderHook(() => useRunways());

    act(() => {
      result.current.addRunway(RunwayModes.MIXED_MODE);
    });

    expect(result.current.runways.length).toBe(1);
    expect(result.current.runways[0].mode).toBe(RunwayModes.MIXED_MODE);
  });

  it("should remove a runway", () => {
    const { result } = renderHook(() => useRunways());

    act(() => {
      result.current.addRunway(RunwayModes.MIXED_MODE);
    });

    const id = result.current.runways[0].id;

    act(() => {
      result.current.removeRunway(id);
    });

    expect(result.current.runways).toEqual([]);
  });

  it("should throw when removing non-existent runway", () => {
    const { result } = renderHook(() => useRunways());
    expect(() => result.current.removeRunway(123)).toThrow(
      "Attempted to remove a runway which does not exist"
    );
  });

  it("should throw on invalid mode", () => {
    const { result } = renderHook(() => useRunways());
    expect(() => result.current.addRunway("invalid")).toThrow(
      "Invalid runway mode"
    );
  });

  it("should not allow more than 10 runways", () => {
    const { result } = renderHook(() => useRunways());

    act(() => {
      for (let i = 0; i < 10; i++) {
        result.current.addRunway(RunwayModes.MIXED_MODE);
      }
    });

    expect(() => result.current.addRunway(RunwayModes.MIXED_MODE)).toThrow(
      "There are already 10 runways being stored (max reached)"
    );
  });

  it("should reset runways", () => {
    const { result } = renderHook(() => useRunways());

    act(() => {
      result.current.addRunway(RunwayModes.MIXED_MODE);
      result.current.resetRunways();
    });

    expect(result.current.runways).toEqual([]);
  });

  it("should import runways correctly", () => {
    const { result } = renderHook(() => useRunways());

    const runwaysToImport = [
      { id: 5, mode: RunwayModes.MIXED_MODE },
      { id: 10, mode: RunwayModes.TAKEOFF_ONLY },
    ];

    act(() => {
      result.current.importRunways(runwaysToImport);
    });

    expect(result.current.runways).toEqual(runwaysToImport);

    // Adding a new runway should continue the counter correctly
    act(() => {
      result.current.addRunway(RunwayModes.LANDING_ONLY);
    });

    expect(result.current.runways[2].id).toBe(11);
  });

  it("should throw on invalid import data", () => {
    const { result } = renderHook(() => useRunways());

    const invalidData = [{ id: "x", mode: "invalid" }];
    expect(() => act(() => result.current.importRunways(invalidData))).toThrow(
      "Invalid runways data"
    );
  });

  it("should remove multiple runways correctly after import", () => {
    const { result } = renderHook(() => useRunways());

    const runwaysToImport = [
      { id: 1, mode: RunwayModes.MIXED_MODE },
      { id: 2, mode: RunwayModes.TAKEOFF_ONLY },
    ];

    act(() => {
      result.current.importRunways(runwaysToImport);
    });

    act(() => {
      result.current.removeRunway(1);
    });

    expect(result.current.runways.length).toBe(1);
    expect(result.current.runways[0].id).toBe(2);

    act(() => {
      result.current.removeRunway(2);
    });

    expect(result.current.runways).toEqual([]);
  });
});


  // Tests for useAdvancedConfig
describe("useAdvancedConfig hook", () => {
	it("should have default values on initialization", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  expect(result.current.advancedConfig).toEqual({
		maxDelayBeforeCancelled: 10,
		fuelThresholdBeforeRedirected: 10,
		timeTakenForTakeoff: 5,
		timeTakenForLanding: 5,
	  });
	});
  
	it("should set maxDelayBeforeCancelled correctly", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  act(() => {
		result.current.setMaxDelayBeforeCancelled(15);
	  });
	  expect(result.current.advancedConfig.maxDelayBeforeCancelled).toBe(15);
	});
  
	it("should throw error for invalid maxDelayBeforeCancelled", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  expect(() => act(() => result.current.setMaxDelayBeforeCancelled(-1))).toThrow();
	  expect(() => act(() => result.current.setMaxDelayBeforeCancelled(3.5))).toThrow();
	  expect(() => act(() => result.current.setMaxDelayBeforeCancelled("abc"))).toThrow();
	});
  
	it("should set fuelThresholdBeforeRedirected correctly", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  act(() => {
		result.current.setFuelThresholdBeforeRedirected(20);
	  });
	  expect(result.current.advancedConfig.fuelThresholdBeforeRedirected).toBe(20);
	});
  
	it("should throw error for invalid fuelThresholdBeforeRedirected", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  expect(() => act(() => result.current.setFuelThresholdBeforeRedirected(-5))).toThrow();
	  expect(() => act(() => result.current.setFuelThresholdBeforeRedirected(4.7))).toThrow();
	  expect(() => act(() => result.current.setFuelThresholdBeforeRedirected("xyz"))).toThrow();
	});
  
	it("should set timeTakenForTakeoff correctly", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  act(() => {
		result.current.setTimeTakenForTakeoff(8);
	  });
	  expect(result.current.advancedConfig.timeTakenForTakeoff).toBe(8);
	});
  
	it("should throw error for invalid timeTakenForTakeoff", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  expect(() => act(() => result.current.setTimeTakenForTakeoff(0))).toThrow();
	  expect(() => act(() => result.current.setTimeTakenForTakeoff(-2))).toThrow();
	  expect(() => act(() => result.current.setTimeTakenForTakeoff(3.2))).toThrow();
	});
  
	it("should set timeTakenForLanding correctly", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  act(() => {
		result.current.setTimeTakenForLanding(7);
	  });
	  expect(result.current.advancedConfig.timeTakenForLanding).toBe(7);
	});
  
	it("should throw error for invalid timeTakenForLanding", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  expect(() => act(() => result.current.setTimeTakenForLanding(0))).toThrow();
	  expect(() => act(() => result.current.setTimeTakenForLanding(-1))).toThrow();
	  expect(() => act(() => result.current.setTimeTakenForLanding(2.5))).toThrow();
	});
  
	it("should reset to default values", () => {
	  const { result } = renderHook(() => useAdvancedConfig());
	  act(() => {
		result.current.setMaxDelayBeforeCancelled(20);
		result.current.setFuelThresholdBeforeRedirected(15);
		result.current.setTimeTakenForTakeoff(6);
		result.current.setTimeTakenForLanding(7);
		result.current.resetAdvancedConfig();
	  });
	  expect(result.current.advancedConfig).toEqual({
		maxDelayBeforeCancelled: 10,
		fuelThresholdBeforeRedirected: 10,
		timeTakenForTakeoff: 5,
		timeTakenForLanding: 5,
	  });
	});
  });

  // Tests for useFlightSchedule
  describe("useFlightSchedule hook", () => {
	it("should initialize with empty flights map", () => {
	  const { result } = renderHook(() => useFlightSchedule());
	  expect(result.current.flights.size).toBe(0);
	});
  
	it("should add a departure flight correctly", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  act(() => {
		result.current.addDepartureFlight("EASYJET", 10, undefined, 12345);
	  });
  
	  const flight = Array.from(result.current.flights.values())[0];
	  expect(flight.callsign).toMatch(/^EASYJET-\d+$/);
	  expect(flight.type).toBe(FlightType.DEPARTURE);
	  expect(flight.expected_departure_time).toBe(10);
	  expect(flight.seed).toBe(12345);
	});
  
	it("should add an arrival flight correctly", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  act(() => {
		result.current.addArrivalFlight(
		  "RYANAIR",
		  EmergencyStatus.NONE,
		  15,
		  12,
		  undefined,
		  54321
		);
	  });
  
	  const flight = Array.from(result.current.flights.values())[0];
	  expect(flight.callsign).toMatch(/^RYANAIR-\d+$/);
	  expect(flight.type).toBe(FlightType.ARRIVAL);
	  expect(flight.expected_arrival_time).toBe(12);
	  expect(flight.remaining_fuel_mins).toBe(15);
	  expect(flight.emergency_status).toBe(EmergencyStatus.NONE);
	  expect(flight.seed).toBe(54321);
	});
  
	it("should increment flight IDs correctly", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  act(() => {
		result.current.addDepartureFlight("EASYJET", 10);
		result.current.addDepartureFlight("RYANAIR", 15);
	  });
  
	  const flights = Array.from(result.current.flights.values());
	  expect(flights[1].id).toBe(flights[0].id + 1);
	});
  
	it("should remove a flight by callsign", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  let callsign;
	  act(() => {
		callsign = result.current.addDepartureFlight("EASYJET", 10).callsign;
	  });
  
	  act(() => {
		result.current.removeFlight(callsign);
	  });
  
	  expect(result.current.flights.size).toBe(0);
	});
  
	it("should throw when removing non-existent flight", () => {
	  const { result } = renderHook(() => useFlightSchedule());
	  expect(() => result.current.removeFlight("INVALID-123")).toThrow(
		"Unable to remove flight, was not found within schedule"
	  );
	});
  
	it("should reset flight schedule", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  act(() => {
		result.current.addDepartureFlight("EASYJET", 10);
		result.current.addArrivalFlight("RYANAIR", EmergencyStatus.NONE, 15, 12);
		result.current.resetFlightSchedule();
	  });
  
	  expect(result.current.flights.size).toBe(0);
	});
  
	it("should import a valid flight schedule", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  const newFlights = [
		{
		  callsign: "EASYJET-1",
		  type: FlightType.DEPARTURE,
		  expected_departure_time: 10,
		  id: 1,
		  seed: 123,
		},
		{
		  callsign: "RYANAIR-2",
		  type: FlightType.ARRIVAL,
		  expected_arrival_time: 15,
		  emergency_status: EmergencyStatus.NONE,
		  remaining_fuel_mins: 20,
		  id: 2,
		  seed: 456,
		},
	  ];
  
	  act(() => {
		result.current.importFlightSchedule(newFlights);
	  });
  
	  expect(result.current.flights.size).toBe(2);
	  expect(result.current.flights.get("EASYJET-1").id).toBe(1);
	  expect(result.current.flights.get("RYANAIR-2").id).toBe(2);
	});
  
	it("should throw when importing invalid flight schedule", () => {
	  const { result } = renderHook(() => useFlightSchedule());
  
	  expect(() =>
		act(() => result.current.importFlightSchedule([{ invalid: true }]))
	  ).toThrow();
	});
  });

    // Tests for useHazardSchedule