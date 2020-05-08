import React, {useEffect} from 'react';
import {withStyles} from "@material-ui/core";
import Typography from "@material-ui/core/Typography";
import {Delete, GroupAdd} from "@material-ui/icons";
import vauthenticatorStyles from "../../component/styles";
import StickyHeadTable from "../../component/StickyHeadTable";
import {deleteClientApplicationFor, findAllClientApplications} from "./ClientAppRepository";
import {Link} from "react-router-dom";
import EditIcon from "@material-ui/icons/Edit";
import AdminTemplate from "../../component/AdminTemplate";
import FormButton from "../../component/FormButton";

const columns = [
    {id: 'clientAppName', label: 'Client Application Name', minWidth: 170},
    {id: 'clientAppId', label: 'Client Application Id', minWidth: 170},
    {id: 'scopes', label: 'Client Scopes', minWidth: 170},
    {id: 'authorizedGrantTypes', label: 'Client Application Autorized Grant Type', minWidth: 170},
    {id: 'federation', label: 'Client Application Federation', minWidth: 170},
    {id: 'edit', label: 'Edit Application', minWidth: 170},
    {id: 'delete', label: 'Delete Application', minWidth: 170}
];

const getEditLinkFor = (clientAppId) => {
    return <Link to={`client-applications/edit/${clientAppId}`}
                 style={{"textDecoration": "none"}}>
        <EditIcon/>
    </Link>;
}

const ClientAppManagementPage = withStyles(vauthenticatorStyles)((props) => {
    const {classes} = props;
    const [applications, setApplications] = React.useState([])

    const getDeleteLinkFor = (clientAppId) => {
        return <Delete onClick={() => {
            deleteClientApplicationFor(clientAppId)
                .then(response => {
                    if (response.status === 204) {
                        fetchAllApplications()
                    }
                })
        }}/>;
    }

    const fetchAllApplications = () => {
        findAllClientApplications()
            .then(val => {
                let rows = val.map(value => {
                    value.edit = getEditLinkFor(value["clientAppId"])
                    value.delete = getDeleteLinkFor(value["clientAppId"])
                    return value
                })
                setApplications(rows)
            });
    }

    useEffect(() => {
        fetchAllApplications()
    }, []);

    return (
        <AdminTemplate maxWidth="lg" classes={classes}>

            <Typography variant="h3" component="h3">
                VAuthenticator Client Application Admin
            </Typography>
            <Link to={"/client-applications/save"}>
                <FormButton type="button"
                            labelPrefix={<GroupAdd fontSize="large"/>}
                            lable={"New Client Application"}/>
            </Link>

            <StickyHeadTable columns={columns} rows={applications}/>

        </AdminTemplate>
    );
})

export default ClientAppManagementPage