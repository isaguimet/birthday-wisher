import React from "react";
import { ThemeProvider } from "styled-components";

const theme = {
    color: {
        ivory: '#fae5df',
        peach: '#f5cac2',
        orange: '#ed7966',
        purple: '#303179',
        darkBlue: '#141850'
    }
};

const Theme = ({ children }) => (
    <ThemeProvider theme={theme}>{children}</ThemeProvider>
);

export default Theme;