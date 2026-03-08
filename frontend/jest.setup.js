import "@testing-library/jest-dom";

// Mantine uses ResizeObserver internally (via ScrollArea); jsdom doesn't provide it
global.ResizeObserver = jest.fn().mockImplementation(() => ({
	observe: jest.fn(),
	unobserve: jest.fn(),
	disconnect: jest.fn(),
}));

// Mantine's Combobox calls scrollIntoView when navigating options; jsdom doesn't implement it
window.HTMLElement.prototype.scrollIntoView = jest.fn();

// Mantine uses matchMedia in some components; jsdom doesn't provide it
Object.defineProperty(window, "matchMedia", {
	writable: true,
	value: jest.fn().mockImplementation((query) => ({
		matches: false,
		media: query,
		onchange: null,
		addListener: jest.fn(),
		removeListener: jest.fn(),
		addEventListener: jest.fn(),
		removeEventListener: jest.fn(),
		dispatchEvent: jest.fn(),
	})),
});
