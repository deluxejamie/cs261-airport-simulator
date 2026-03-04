import { renderHook, act } from "@testing-library/react";
import { useRunways, RunwayModes } from "./hooks";

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