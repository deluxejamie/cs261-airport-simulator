import { renderHook } from "@testing-library/react";
import { useRunways } from "./hooks";

// See reference: https://testing-library.com/docs/react-testing-library/api/#renderhook
describe("runway reducers", () => {
	it("should return default state", () => {
		const { result } = renderHook(() => useRunways());
		const { runways, addRunway, removeRunway } = result.current;
		expect(runways).toEqual([]);
	});
});
