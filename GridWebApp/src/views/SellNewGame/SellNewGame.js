import React, { Component } from 'react';
import classNames from "classnames";
import styles from "assets/jss/material-kit-react/views/landingPage.js";
import 'assets/css/hide.css'
import { withStyles } from '@material-ui/styles';
import GridContainer from "components/Grid/GridContainer.js";
import GridItem from "components/Grid/GridItem.js";
import LoggedHeader from "components/MyHeaders/LoggedHeader.js"
import NewGame from "./Sections/NewGame";

// Global Variables
import baseURL from '../../variables/baseURL'
import global from "../../variables/global";


class SellNewGame extends Component {

    constructor(props) {

        super(props);
    }


    render() {
        const { classes } = this.props;

        return (
            <div>
                <LoggedHeader user={global.user} cart={global.cart} heightChange={false} height={600} />

                <div className={classNames(classes.main)} style={{ marginTop: "60px" }}>

                    <div className={classes.container}>
                        <div style={{ padding: "70px 0" }}>
                            <GridContainer>
                                <GridItem xs={12} sm={12} md={12}>
                                    <div style={{ textAlign: "left" }}>
                                        <GridContainer>
                                            <GridItem xs={12} sm={12} md={9}>
                                                <h2 style={{
                                                    color: "#999",
                                                    fontWeight: "bolder",
                                                    marginTop: "0px",
                                                    padding: "0 0"
                                                }}>Sell a new game</h2>
                                            </GridItem>
                                        </GridContainer>
                                        <hr style={{ color: "#999", opacity: "0.4" }}></hr>

                                        <GridContainer>
                                            <GridItem xs={12} sm={12} md={12}>
                                                <NewGame />
                                            </GridItem>
                                        </GridContainer>

                                    </div>
                                </GridItem>
                            </GridContainer>
                        </div>
                    </div>



                </div>


            </div>
        )
    }
}


export default withStyles(styles)(SellNewGame);