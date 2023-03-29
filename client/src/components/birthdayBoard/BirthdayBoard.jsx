import {Board, BoardContainer} from "../../pages/ProfilePage.style";
import BirthdayCard from "../birthdayCard/BirthdayCard";
import {useState} from "react";
import {useSelector} from "react-redux";
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Switch from "@mui/material/Switch"
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import Button from '@mui/material/Button';
import axiosInstance from "../../utils/API";

/**
 * A component for rendering a User Birthday Board.
 * @param props {Object} Expected props for this component:
 * - boardId {string} the unique identifier for this board in the database.
 * - year {string} the year that this board is for.
 * - open {boolean} whether the board is open or closed (for people to submit messages to).
 * - public {boolean} whether the board is public or private (visibility from friends).
 * - messages {object} birthday messages that belong to this board.
 * - setLoading {function} function to set loading in a parent component.
 * - setData {function} function to set data in a parent component.
 * - setError {function} function to set error in a parent component.
 * - profileUser {string} the unique identifier for the user whose board this component is displaying.
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayBoard = (props) => {
    // the ID of the user that is accessing this page
    const loggedInUser = useSelector((state) => state.user.id);
    const loggedInUserIsProfileUser = loggedInUser === props.profileUser;

    const [isOpen, setOpen] = useState(props.open === true);
    const [isPublic, setPublic] = useState(props.public === true);

    const [isEditing, setEditing] = useState(false);
    const [input, setInput] = useState("");

    const handleChange = (event) => {
        setInput(event.target.value);
    };

    const handleSubmit = (event) => {
        const data = {
            fromUserId: loggedInUser,
            toUserId: props.profileUser,
            msgText: input,
        };
        props.setLoading(true);
        axiosInstance.post(`http://localhost:8080/boards/${props.boardId}/messages`, data).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err8080) => {
            if (err8080.response) {
                props.setLoading(false);
                props.setError(err8080.response.data);
            } else {
                axiosInstance.post(`http://localhost:8081/boards/${props.boardId}/messages`, data).then((response) => {
                    props.setLoading(false);
                    props.setData(response.data);
                    props.setError(null);
                }).catch((err) => {
                    props.setLoading(false);
                    //props.setData(null);
                    if (err.response) {
                        props.setError(err.response.data);
                    } else {
                        props.setError(err.message);
                    }
                });
            }
        });
    };

    const togglePublic = () => {
        props.setLoading(true);
        if (isPublic) {
            axiosInstance.patch(`http://localhost:8080/boards/setPrivate/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setPublic(false);
            }).catch((err8080) => {
                if (err8080.response) {
                    props.setLoading(false);
                    props.setError(err8080.response.data);
                } else {
                    axiosInstance.patch(`http://localhost:8081/boards/setPrivate/${props.boardId}`).then((response) => {
                        props.setLoading(false);
                        props.setData(response.data);
                        props.setError(null);
                        setPublic(false);
                    }).catch((err) => {
                        props.setLoading(false);
                        //props.setData(null);
                        if (err.response) {
                            props.setError(err.response.data);
                        } else {
                            props.setError(err.message);
                        }
                    });
                }
            });
        } else {
            axiosInstance.patch(`http://localhost:8080/boards/setPublic/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setPublic(true);
            }).catch((err8080) => {
                if (err8080.response) {
                    props.setLoading(false);
                    props.setError(err8080.response.data);
                } else {
                    axiosInstance.patch(`http://localhost:8081/boards/setPublic/${props.boardId}`).then((response) => {
                        props.setLoading(false);
                        props.setData(response.data);
                        props.setError(null);
                        setPublic(true);
                    }).catch((err) => {
                        props.setLoading(false);
                        //props.setData(null);
                        if (err.response) {
                            props.setError(err.response.data);
                        } else {
                            props.setError(err.message);
                        }
                    });
                }
            });
        }
    };

    const toggleOpen = () => {
        props.setLoading(true);
        if (isOpen) {
            axiosInstance.patch(`http://localhost:8080/boards/setClosed/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setOpen(false);
            }).catch((err8080) => {
                if (err8080.response) {
                    props.setLoading(false);
                    props.setError(err8080.response.data);
                } else {
                    axiosInstance.patch(`http://localhost:8081/boards/setClosed/${props.boardId}`).then((response) => {
                        props.setLoading(false);
                        props.setData(response.data);
                        props.setError(null);
                        setOpen(false);
                    }).catch((err) => {
                        props.setLoading(false);
                        //props.setData(null);
                        if (err.response) {
                            props.setError(err.response.data);
                        } else {
                            props.setError(err.message);
                        }
                    });
                }
            });
        } else {
            axiosInstance.patch(`http://localhost:8080/boards/setOpen/${props.boardId}`).then((response) => {
                props.setLoading(false);
                props.setData(response.data);
                props.setError(null);
                setOpen(true);
            }).catch((err8080) => {
                if (err8080.response) {
                    props.setLoading(false);
                    props.setError(err8080.response.data);
                } else {
                    axiosInstance.patch(`http://localhost:8081/boards/setOpen/${props.boardId}`).then((response) => {
                        props.setLoading(false);
                        props.setData(response.data);
                        props.setError(null);
                        setOpen(true);
                    }).catch((err) => {
                        props.setLoading(false);
                        //props.setData(null);
                        if (err.response) {
                            props.setError(err.response.data);
                        } else {
                            props.setError(err.message);
                        }
                    });
                }
            });
        }
    };

    const deleteBoard = () => {
        props.setLoading(true);
        axiosInstance.delete(`http://localhost:8080/boards/${props.boardId}`).then((response) => {
            props.setLoading(false);
            props.setData(response.data);
            props.setError(null);
        }).catch((err8080) => {
            if (err8080.response) {
                props.setLoading(false);
                props.setError(err8080.response.data);
            } else {
                axiosInstance.delete(`http://localhost:8081/boards/${props.boardId}`).then((response) => {
                    props.setLoading(false);
                    props.setData(response.data);
                    props.setError(null);
                }).catch((err) => {
                    props.setLoading(false);
                    //props.setData(null);
                    if (err.response) {
                        props.setError(err.response.data);
                    } else {
                        props.setError(err.message);
                    }
                });
            }
        });
    };

    return (
        <>
            <BoardContainer>
                <h3>{props.year}</h3>
                
        
                {loggedInUserIsProfileUser || (!loggedInUserIsProfileUser && isPublic) ? (
                    <>
                        {loggedInUserIsProfileUser && (
                            <div>
                                <FormGroup style={{display: "inline"}}>
                                    <FormControlLabel control={<Switch checked={isPublic} onChange={togglePublic}/>}
                                                      label="Public"/>
                                </FormGroup>
                                <FormGroup style={{display: "inline"}}>
                                    <FormControlLabel control={<Switch checked={isOpen} onChange={toggleOpen}/>}
                                                      label="Open"/>
                                </FormGroup>
                                <div style={{display: "inline"}}>
                                <Button variant={"contained"} color={"error"} onClick={deleteBoard}>Delete</Button>
                                </div>

                            </div>
                        )}

                        {!loggedInUserIsProfileUser && <>
                            {isEditing ? (
                                <form onSubmit={handleSubmit}>
                                    <input type={"text"} value={input} onChange={handleChange}/>
                                    <input type={"submit"} value={"Submit"}/>
                                    
                                     {/* <div Style="padding-top:0.3rem;">
                    <Button variant="outlined" size="small" type={"submit"}>Submit</Button>
                </div> */}
                                </form>
                            ) : (
                                <>
                                    {isOpen ? (
                                        <Button variant="contained" size="small" onClick={() => setEditing(true)}>Add Wish</Button>
                                    ) : (
                                        <p>Submissions are closed for this board.</p>
                                    )}
                                </>
                            )}
                        </>}

                        {/* if no item it doesn't render anything, if no item it should just render blank */}
                        <Board>
                            {Object.entries(props.messages).map(([msgId, msg]) => (
                                <BirthdayCard
                                    key={msgId}
                                    id={msgId}
                                    boardId={props.boardId}
                                    msgId={msgId}
                                    fromUserId={msg.fromUserId}
                                    toUserId={msg.toUserId}
                                    lastUpdatedDate={msg.lastUpdatedDate}
                                    msgText={msg.msgText}
                                    setLoading={props.setLoading}
                                    setData={props.setData}
                                    setError={props.setError}
                                    profileUser={props.profileUser}
                                />
                            ))}
                        </Board>
                    </>
                ) : (
                    <Board>
                        <LockOutlinedIcon/>
                        <p>This board is private.</p>
                    </Board>
                )}
            </BoardContainer>
        </>
    );
};

export default BirthdayBoard;