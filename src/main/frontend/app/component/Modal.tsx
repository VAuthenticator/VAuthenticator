import React from "react";

import FormButton from "../component/FormButton";
import Separator from "../component/Separator";
import {Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle} from "@mui/material";
import {Breakpoint} from "@mui/system";

interface ConfirmationDialogProps {
    onExecute: () => void,
    onClose: () => void,
    open: boolean,
    title: string,
    message: string,
    maxWidth: Breakpoint
}

const Modal: React.FC<ConfirmationDialogProps> = ({
                                                                   onExecute,
                                                                   onClose,
                                                                   open,
                                                                   title,
                                                                   message,
                                                                   maxWidth
                                                               }) => {
    return (
        <Dialog onClose={onClose} aria-labelledby="simple-dialog-title" open={open} maxWidth={maxWidth}>
            <DialogTitle id="simple-dialog-title">{title}</DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    {message}
                </DialogContentText>

                <Separator/>

                <DialogActions>
                    <FormButton label="Yes" type={"button"} onClickHandler={onExecute}/>
                    <FormButton label="No" type={"button"} onClickHandler={onClose}/>
                </DialogActions>
            </DialogContent>
        </Dialog>
    );
}

export default Modal