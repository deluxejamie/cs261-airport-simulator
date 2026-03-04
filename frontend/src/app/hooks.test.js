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
});