import Container from "react-bootstrap/esm/Container";
import Theme from "../../theme/Theme";
import {Navlink, StyledImg, StyledLogo, StyledNavbar} from "./NavBar.style";
import Nav from 'react-bootstrap/Nav'
import Icons from '../../Icons';
import {useSelector} from "react-redux";

const NavBar = () => {
    const user = useSelector((state) => state.user);
    return (
        <Theme>
            <StyledNavbar>
                <Container>
                    <StyledLogo href="/">Birthday Wisher</StyledLogo>
                    {user && (
                        <Nav>
                            <Navlink href="/wishing-center"><StyledImg src={Icons[0]}/></Navlink>
                            <Navlink href="/friends"><StyledImg src={Icons[1]}/></Navlink>
                            <Navlink href="/notification"><StyledImg src={Icons[2]}/></Navlink>
                            <Navlink href={`/profile/${user.id}`}><StyledImg src={Icons[3]}/></Navlink>
                        </Nav>
                    )}
                </Container>
            </StyledNavbar>
        </Theme>
    );
};

export default NavBar;
