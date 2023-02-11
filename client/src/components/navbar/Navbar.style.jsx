import styled from 'styled-components';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

export const StyledNavbar = styled(Navbar)`
    background-color: rgba(48, 49, 121, 0.92);
`;

export const StyledLogo = styled(Navbar.Brand)`
    color: ${props => props.theme.color.ivory};
`;

// export const NavItems = styled(Nav)`
//     justify-content: end;
// `;

export const Navlink = styled(Nav.Link)`
    color: ${props => props.theme.color.ivory};
`;

// https://dev.to/aromanarguello/how-to-use-themes-in-styled-components-49h