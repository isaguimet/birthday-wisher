import styled from 'styled-components';

export const StyledDiv = styled.div`
    padding-right: 15rem;
    padding-left: 15rem;
`;

export const Profile = styled.div`
    display: flex;
    justify-content: center;
    padding-top: 2rem;
`;

export const Board = styled.div`
    display: flex;
    justify-content: left
    
`;

export const BoardContainer = styled.div`
    padding-top: 5rem;
`;

export const ProfileInfo = styled.div`
    display: inline;
`;

export const Age = styled.div`
    display: inline;
    background-color: ${props => props.theme.color.peach};  
    border-radius: 10%;
`;
