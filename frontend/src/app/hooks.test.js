import { renderHook, act } from "@testing-library/react";
import { useRunways, RunwayModes } from "./hooks";

// See reference: https://testing-library.com/docs/react-testing-library/api/#renderhook
describe("useRunways", () => {
	it("should start empty", () => {
		const { result } = renderHook(() => useRunways());
		expect(result.current.runways).toEqual([]);
	});

	it("should add runway", () => {
		const { result } = renderHook(() => useRunways());

		act(() => {
			result.current.addRunway(RunwayModes.MIXED_MODE);
		});

		expect(result.current.runways.length).toBe(1);
	});

	it("should remove runway", () => {
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

	it("should throw on invalid mode", () => {
		const { result } = renderHook(() => useRunways());

		expect(() => result.current.addRunway("invalid")).toThrow();
	});

	it("should not allow more than 10 runways", () => {
		const { result } = renderHook(() => useRunways());

		act(() => {
			for (let i = 0; i < 10; i++) {
				result.current.addRunway(RunwayModes.MIXED_MODE);
			}
		});

		expect(() => result.current.addRunway(RunwayModes.MIXED_MODE)).toThrow();
	});
});
