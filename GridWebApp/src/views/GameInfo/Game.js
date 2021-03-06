
import React, { Component } from 'react';
import classNames from "classnames";

// Global Variables
import baseURL from '../../variables/baseURL'
import global from "../../variables/global";

// Styles
import styles from "assets/jss/material-kit-react/views/landingPage.js";
import 'assets/css/hide.css'

// Core MaterialUI 
import { withStyles } from '@material-ui/styles';
import CardMedia from '@material-ui/core/CardMedia';
import CardContent from '@material-ui/core/CardContent';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardActionArea from '@material-ui/core/CardActionArea';
import Pagination from '@material-ui/lab/Pagination';

import InputAdornment from "@material-ui/core/InputAdornment";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import Radio from "@material-ui/core/Radio";
import Switch from "@material-ui/core/Switch";

import Typography from '@material-ui/core/Typography';

import ExpansionPanel from '@material-ui/core/ExpansionPanel';
import ExpansionPanelSummary from '@material-ui/core/ExpansionPanelSummary';
import ExpansionPanelDetails from '@material-ui/core/ExpansionPanelDetails'
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

// Core Components
import Header from "components/Header/Header.js";
import Footer from "components/Footer/Footer.js";
import GridContainer from "components/Grid/GridContainer.js";
import GridItem from "components/Grid/GridItem.js";
import Button from "components/CustomButtons/Button.js";
import HeaderLinks from "components/Header/HeaderLinks.js";
import Parallax from "components/Parallax/Parallax.js";
import CustomInput from "components/CustomInput/CustomInput.js";
import CardHeader from 'components/Card/CardHeader.js';


import LoggedHeader from "components/MyHeaders/LoggedHeader.js"

// React Select
import Select from 'react-select';

// MaterialUI Icons
import Check from "@material-ui/icons/Check";
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

// Images
import image1 from "assets/img/bg.jpg";
import image4 from "assets/img/NFS-Heat.jpg";
import image from "assets/img/favicon.png";


// Loading Animation
import FadeIn from "react-fade-in";
import Lottie from "react-lottie";
import * as loadingAnim from "assets/animations/loading_anim.json";


// Toastify
import 'react-toastify/dist/ReactToastify.css';
import { ToastContainer, toast, Flip } from 'react-toastify';

import {
    Link,
    Redirect
} from "react-router-dom";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import IconButton from "@material-ui/core/IconButton";
import Close from "@material-ui/icons/Close";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import stylesjs from "assets/jss/material-kit-react/views/componentsSections/javascriptStyles.js";
import stylesbasic from "assets/jss/material-kit-react/views/componentsSections/basicsStyle.js";
import Slide from "@material-ui/core/Slide";
import EuroSymbolIcon from '@material-ui/icons/EuroSymbol';

const Transition = React.forwardRef(function Transition(props, ref) {
    return <Slide direction="down" ref={ref} {...props} />;
});

Transition.displayName = "Transition";

class Game extends Component {
    constructor() {
        super();
    }

    state = {
        doneLoading: false,
        animationOptions: {
            loop: true, autoplay: true, animationData: loadingAnim.default, rendererSettings: {
                preserveAspectRatio: "xMidYMid slice"
            }
        },
        game: null,
        redirectLogin: false,
        redirectGames: false,

        loadingSell: false,
        loadingAuctions: false,
        loadingReviews: false,

        sellListings: [],
        auctionsListings: [],
        currentBiding: null,
        auctionBidModal: false,
        listingsPage: 1,
        noListingPages: 1,
        noSells: 0,

        reviewListings: [],
        reviewsPage: 1,
        noReviewPages: 1,
        noReviews: 0
    }

    async getGameInfo() {
        var login_info = null
        if (global.user != null) {
            login_info = global.user.token
        }

        await this.setState({ gamesLoaded: false })

        // Get All Games
        await fetch(baseURL + "grid/games/game?id=" + this.props.match.params.game, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: login_info
            }
        })
            .then(response => {
                if (response.status === 401) {
                    return response
                } else if (response.status === 200) {
                    return response.json()
                }
                else throw new Error(response.status);
            })
            .then(data => {
                if (data.status === 401) { // Wrong token
                    localStorage.setItem('loggedUser', null);
                    global.user = JSON.parse(localStorage.getItem('loggedUser'))

                    this.setState({
                        redirectLogin: true
                    })

                } else {
                    var description = data.description
                    description = description.substring(3, description.length - 4)

                    description = description.replace(/&#39;s/g, "'s")
                    description = description.replace(/<p>/g, "\n")
                    description = description.replace(/<\/p>/g, "")
                    description = description.replace(/<h3>/g, "\n")
                    description = description.replace(/<\/h3>/g, "")
                    description = description.replace(/<br \/>/g, "\n")

                    var minimizedDescription = description
                    if (description.length > 315) {
                        minimizedDescription = description.substring(0, 312) + "..."
                    }
                    data.minimizedDescription = minimizedDescription
                    data.description = description

                    var allDevelopers = ""
                    data.developers.forEach(developer => {
                        allDevelopers += developer.name + ", "
                    })
                    allDevelopers = allDevelopers.substring(0, allDevelopers.length - 2)
                    data["allDevelopers"] = allDevelopers


                    var allGenres = ""
                    data.gameGenres.forEach(genre => {
                        allGenres += genre.name + ", "
                    })
                    allGenres = allGenres.substring(0, allGenres.length - 2)
                    data["allGenres"] = allGenres

                    var allPlatforms = ""
                    data.platforms.forEach(platform => {
                        allPlatforms += platform + ", "
                    })
                    allPlatforms = allPlatforms.substring(0, allPlatforms.length - 2)
                    data["allPlatforms"] = allPlatforms

                    this.setState({ game: data })
                }
            })
            .catch(error => {
                console.log(error)
                toast.error('Sorry, an unexpected error has occurred while loading that game\'s information...', {
                    position: "top-center",
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    toastId: "errorToast"
                });
                this.setState({ redirectGames: true })
            });

    }

    async getAuctionListings() {
        var login_info = null
        if (global.user != null) {
            login_info = global.user.token
        }

        await this.setState({ loadingAuctions: true });

        await fetch(baseURL + "grid/auction?gameId=" + this.props.match.params.game, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: login_info
            }
        }).then(response => {
            if (response.status === 401) {
                return response
            } else if (response.status === 200) {
                return response.json()
            } else throw new Error(response.status);
        }).then(data => {
            if (data.status === 401) { // Wrong token
                localStorage.setItem('loggedUser', null);
                global.user = JSON.parse(localStorage.getItem('loggedUser'))

                this.setState({
                    redirectLogin: true
                })

            } else {

                let new_data = {};
                for (let i = 0; i < data.length; i++) {
                    const entry = data[i];

                    let buyerColor = "#fcdf03";

                    let buyer = "None";
                    if (entry.buyer !== null) {
                        buyer = entry.buyer;
                        if (buyer === global.user.username)
                            buyerColor = "#0ffc03";
                        else
                            buyerColor = "#fc0303";
                    }

                    new_data[entry.id] = {
                        "auctioneer": entry.auctioneer,
                        "buyer": entry.buyer,
                        "buyerTxt": buyer,
                        "endDate": entry.endDate,
                        "gameKey": entry.gameKey,
                        "price": entry.price,
                        "startDate": entry.startDate,
                        "buyerColor": buyerColor
                    }

                }
                this.setState({ auctionsListings: new_data });
            }
        }).catch(error => {
            console.log(error)
            toast.error('Sorry, an unexpected error has occurred while loading the sales for this game!', {
                position: "top-center",
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                toastId: "errorToast"
            });
        });


        await this.setState({ loadingAuctions: false });
    }



    async getGameListings() {
        var login_info = null
        if (global.user != null) {
            login_info = global.user.token
        }

        await this.setState({ loadingSell: true })

        // Get All Games
        await fetch(baseURL + "grid/sell-listing?gameId=" + this.props.match.params.game + "&page=0" + (this.state.listingsPage - 1), {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: login_info
            }
        })
            .then(response => {
                if (response.status === 401) {
                    return response
                } else if (response.status === 200) {
                    return response.json()
                }
                else throw new Error(response.status);
            })
            .then(data => {
                if (data.status === 401) { // Wrong token
                    localStorage.setItem('loggedUser', null);
                    global.user = JSON.parse(localStorage.getItem('loggedUser'))

                    this.setState({
                        redirectLogin: true
                    })

                } else {

                    if (data.first) {
                        this.setState({
                            noListingPages: data.totalPages,
                            noSells: data.totalElements
                        })
                    }

                    var content = []

                    var co = -1

                    data.content.forEach(sell => {
                        var listing = sell

                        co++

                        var inCart = false
                        if (global.cart != null) {
                            for (var i = 0; i < global.cart.games.length; i++) {
                                var game = global.cart.games[i]

                                if (listing.id == game.id) {
                                    inCart = true
                                    break
                                }
                            }
                        }

                        if (global.user != null) {
                            if (!inCart) {
                                listing['cart'] = <Button
                                    size="md"
                                    style={{ backgroundColor: "#4ec884" }}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    id={"addToCartButton" + co}
                                    onClick={() => this.addToCart(sell)}
                                >
                                    <i class="fas fa-cart-arrow-down"></i> Add to Cart
                            </Button>
                            } else {
                                listing['cart'] = <Button
                                    size="md"
                                    style={{ backgroundColor: "#ff3ea0" }}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    onClick={() => this.removeFromCart(sell)}
                                    id={"removeFromCartButton" + co}

                                >
                                    <i class="fas fa-cart-arrow-down"></i> Remove from Cart
                            </Button>
                            }
                        } else {
                            if (!inCart) {
                                listing['cart'] = <Button
                                    size="md"
                                    style={{ backgroundColor: "#4ec884" }}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    disabled
                                    id="addToCartButton"
                                >
                                    <i class="fas fa-cart-arrow-down"></i> Add to Cart
                            </Button>
                            } else {
                                listing['cart'] = <Button
                                    size="md"
                                    style={{ backgroundColor: "#ff3ea0" }}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    disabled
                                    id="removeFromCartButton"
                                >
                                    <i class="fas fa-cart-arrow-down"></i> Remove from Cart
                            </Button>
                            }
                        }

                        content.push(listing)
                    })

                    this.setState({
                        sellListings: content
                    })
                }
            })
            .catch(error => {
                console.log(error)
                toast.error('Sorry, an unexpected error has occurred while loading the sales for this game!', {
                    position: "top-center",
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    toastId: "errorToast"
                });
            });

        await this.setState({ loadingSell: false })


    }

    async getGameReviews() {
        var login_info = null
        if (global.user != null) {
            login_info = global.user.token
        }

        await this.setState({ loadingReviews: true })

        // Get All Games
        await fetch(baseURL + "grid/reviews/game?game_id=" + this.props.match.params.game + "&page=" + (this.state.reviewsPage - 1), {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: login_info
            }
        })
            .then(response => {
                if (response.status === 401) {
                    return response
                } else if (response.status === 200) {
                    return response.json()
                }
                else throw new Error(response.status);
            })
            .then(data => {
                if (data.status === 401) { // Wrong token
                    localStorage.setItem('loggedUser', null);
                    global.user = JSON.parse(localStorage.getItem('loggedUser'))

                    this.setState({
                        redirectLogin: true
                    })

                } else {

                    if (data.first) {
                        this.setState({
                            noReviewPages: data.totalPages,
                            noReviews: data.totalElements
                        })
                    }

                    var content = []

                    data.content.forEach(review => {
                        var score = <span style={{ color: "#999" }}>UNRATED</span>

                        if (review.score == -1) {
                            score = <span style={{ color: "#999" }}>UNRATED</span>
                        }
                        else if (review.score > 0 && review.score <= 1) {
                            score = <span style={{ color: "red" }}><b>{review.score} <i class="far fa-star"></i></b></span>
                        } else if (review.score < 4) {
                            score = <span style={{ color: "#fc926e" }}><b>{review.score} <i class="far fa-star"></i></b></span>
                        } else if (review.score <= 5) {
                            score = <span style={{ color: "#4ec884" }}><b>{review.score} <i class="far fa-star"></i></b></span>
                        }

                        review["score"] = score

                        content.push(review)
                    })

                    this.setState({
                        reviewListings: content
                    })
                }
            })
            .catch(error => {
                console.log(error)
                toast.error('Sorry, an unexpected error has occurred while loading the reviews for this game!', {
                    position: "top-center",
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    toastId: "errorToast"
                });
            });

        await this.setState({ loadingReviews: false })


    }

    async addToWishlist() {
        var login_info = null
        if (global.user != null) {
            login_info = global.user.token
        }

        await this.setState({ doneLoading: false })

        // Get All Games
        await fetch(baseURL + "grid/wishlist?game_id=" + this.state.game.id + "&user_id=" + global.user.id, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: login_info
            }
        })
            .then(response => {
                if (response.status === 401) {
                    return response
                } else if (response.status === 200) {
                    return response.json()
                }
                else throw new Error(response.status);
            })
            .then(data => {

                if (data.status === 401) { // Wrong token
                    localStorage.setItem('loggedUser', null);
                    global.user = JSON.parse(localStorage.getItem('loggedUser'))

                    this.setState({
                        doneLoading: true,
                        redirectLogin: true
                    })

                } else {
                    this.setState({ doneLoading: true })

                    toast.success('Game successfully added to your wishlist!', {
                        position: "top-center",
                        hideProgressBar: false,
                        closeOnClick: true,
                        pauseOnHover: true,
                        draggable: true,
                        toastId: "successWishlistAdd"
                    });
                }

            })
            .catch(error => {
                console.log(error)
                this.setState({ doneLoading: true })

                toast.error('Sorry, an unexpected error has occurred!', {
                    position: "top-center",
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    toastId: "errorToast"
                });
            });


    }

    async addToCart(game) {
        await this.setState({ doneLoading: false })

        var cart = []
        if (global.cart != null) {
            cart = global.cart.games
        }
        cart.push(game)
        localStorage.setItem('cart', JSON.stringify({ "games": cart }));
        global.cart = JSON.parse(localStorage.getItem('cart'))

        await this.getGameListings()

        toast.success('Sale successfully added to shopping cart!', {
            position: "top-center",
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            toastId: "addToCartToast"
        });

        this.setState({ doneLoading: true })
    }

    async removeFromCart(game) {
        await this.setState({ doneLoading: false })

        var cart = []
        if (global.cart != null) {
            for (var i = 0; i < global.cart.games.length; i++) {
                var foundGame = global.cart.games[i]
                if (game.id != foundGame.id) {
                    cart.push(foundGame)
                }
            }
        }

        await localStorage.setItem('cart', JSON.stringify({ "games": cart }));
        global.cart = await JSON.parse(localStorage.getItem('cart'))

        await this.getGameListings()

        toast.success('Sale successfully removed to shopping cart!', {
            position: "top-center",
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            toastId: "removeFromCartToast"
        });

        this.setState({ doneLoading: true })
    }

    async componentDidMount() {
        window.scrollTo(0, 0)
        await this.getGameInfo()
        await this.getGameListings()
        await this.getGameReviews()
        await this.getAuctionListings();
        this.setState({ doneLoading: true })
    }

    changePageSales = async (event, value) => {
        await this.setState({
            listingsPage: value
        })

        console.log(this.state.listingsPage)

        await this.getGameListings()

    };

    changePageReviews = async (event, value) => {
        await this.setState({
            reviewsPage: value
        })

        await this.getGameReviews()

    };

    renderRedirectLogin = () => {
        if (this.state.redirectLogin) {
            return <Redirect to='/login-page' />
        }
    }

    renderRedirectGames = () => {
        if (this.state.redirectGames) {
            return <Redirect to='/games' />
        }
    }

    renderRedirectProfile = () => {
        if (this.state.redirectProfile != null && this.state.redirectProfile != "") {
            return <Redirect to={'/user/' + this.state.redirectProfile} />
        }
    }

    goToProfile = (profile) => {
        this.setState({ redirectProfile: profile })
    }


    setClassicModal = (status, event) => {
        const { auctionBidModal } = this.state;
        this.setState({
            auctionBidModal: !auctionBidModal
        });
        if (event === undefined) return;

        if (status) {
            const id = parseInt(event.currentTarget.id);
            const currentBiding = this.state.auctionsListings[id];
            currentBiding["id"] = id;
            this.setState({
                currentBiding: currentBiding
            });
        } else {
            this.setState({
                currentBiding: null
            });
        }
    };


    submitBid = async (event) => {
        const { currentBiding } = this.state;
        if (currentBiding === null) return;
        const id = parseInt(event.currentTarget.id);
        const newPrice = parseFloat(document.getElementById("bid" + id).value);

        if (isNaN(newPrice)) {
            toast.error('You should insert a valid number!', {
                position: "top-center",
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                toastId: "errorToast"
            });
            return;
        }
        if (newPrice <= currentBiding.price) {
            toast.error('Bid price should be greater than actual price!', {
                position: "top-center",
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                toastId: "errorToast"
            });
            return;
        }

        if (newPrice > global.user.funds) {
            toast.error('Not enough money! :(', {
                position: "top-center",
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                toastId: "errorToast"
            });
            return;
        }

        let errorStatus = false;
        const data = {
            "user": global.user.username,
            "gameKey": currentBiding.gameKey,
            "price": newPrice
        };

        console.log(data);
        let login_info = null;

        if (global.user != null) {
            login_info = global.user.token
        }

        await fetch(baseURL + "grid/bidding", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: login_info
            },
            body: JSON.stringify(data)
        }).then(response => {
            if (response.status === 401 || response.status === 400) {
                return response
            } else if (response.status === 200) {
                return response.json()
            } else throw new Error(response.status);
        }).then(data => {
            if (data.status === 401) { // Wrong token
                localStorage.setItem('loggedUser', null);
                global.user = JSON.parse(localStorage.getItem('loggedUser'));

                this.setState({
                    redirectLogin: true
                })
            }
            else if (data.status === 400) { // Wrong token
                toast.error('Oops, you can\'t participate on your own auction silly!!', {
                    position: "top-center",
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    toastId: "errorToast"
                });
                errorStatus = true;
            }
        }).catch(error => {
            toast.error('Sorry, an unexpected error has occurred when adding a bid!', {
                position: "top-center",
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                toastId: "errorToast"
            });
            errorStatus = true;
            console.log(error);
        });

        this.setClassicModal(false);
        this.setState({
            currentBiding: null
        });

        if (errorStatus) return;

        toast.success('Bid added with success', {
            position: "top-center",
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            toastId: "success"
        });



        await this.getAuctionListings();

    };


    renderModal() {
        const { classes } = this.props;

        const { currentBiding } = this.state;
        if (currentBiding === null) return;

        let hiddenKey = "";
        for (let i = 0; i < currentBiding.gameKey.length; i++)
            hiddenKey += "*";

        let buyerColor = "#fcdf03";

        let buyer = "None";
        if (currentBiding.buyer !== null) {
            buyer = currentBiding.buyer;
            if (buyer === global.user.username)
                buyerColor = "#0ffc03";
            else
                buyerColor = "#fc0303";
        }


        return (
            <Dialog
                classes={{
                    root: classes.center,
                    paper: classes.modal
                }}
                open={this.state.auctionBidModal}
                TransitionComponent={Transition}
                keepMounted
                onClose={() => this.setClassicModal(false)}
                aria-labelledby="classic-modal-slide-title"
                aria-describedby="classic-modal-slide-description"
            >
                <DialogTitle
                    id="classic-modal-slide-title"
                    disableTypography
                    className={classes.modalHeader}
                >
                    <IconButton
                        className={classes.modalCloseButton}
                        key="close"
                        aria-label="Close"
                        color="inherit"
                        onClick={() => this.setClassicModal(false)}
                    >
                        <Close className={classes.modalClose} />
                    </IconButton>

                </DialogTitle>
                <DialogContent
                    id="classic-modal-slide-description"
                    className={classes.modalBody}
                >

                    <GridContainer>

                        <GridItem xs={12} sm={12} md={12}>
                            <div style={{ textAlign: "left" }}>
                                <h3 style={{ color: "#3b3e48", fontWeight: "bolder" }}><b
                                    style={{ color: "#3b3e48" }}>{this.state.game.name}</b></h3>
                                <hr style={{ color: "#999" }}></hr>
                            </div>
                            <div style={{ textAlign: "left", marginTop: "30px" }}>
                                <span style={{ color: "#999", fontSize: "20px", "align-items": "center" }}>
                                    <b>GameKey:</b> <span
                                        style={{ color: "#3b3e48" }}> {hiddenKey}</span>
                                </span>
                            </div>
                            <div style={{ textAlign: "left", marginTop: "30px" }}>
                                <span style={{ color: "#999", fontSize: "20px", "align-items": "center" }}>
                                    <b>Current Price:</b> <span
                                        style={{ color: "#3b3e48" }}> {currentBiding.price} €</span>
                                </span>
                            </div>
                            <div style={{ textAlign: "left", marginTop: "30px" }}>
                                <span style={{ color: "#999", fontSize: "20px", "align-items": "center" }}>
                                    <b>Actual Winner:</b> <span
                                        style={{ color: buyerColor }}> <b>{buyer}</b></span>
                                </span>
                            </div>
                        </GridItem>


                    </GridContainer>


                </DialogContent>
                <DialogActions className={classes.modalFooter}>

                    <CustomInput
                        labelText="Price..."
                        id={"bid" + currentBiding.id}

                        formControlProps={{
                            fullWidth: true
                        }}
                        inputProps={{
                            endAdornment: (
                                <InputAdornment position="end">
                                    <EuroSymbolIcon />
                                </InputAdornment>
                            )
                        }}
                    />&nbsp;
          <Button
                        size="md"
                        style={{ backgroundColor: "#4ec884" }}
                        target="_blank"
                        rel="noopener noreferrer"
                        onClick={this.submitBid}
                        id={currentBiding.id}
                    >
                        <i class="fas fa-gavel"></i> Make a Bidding
          </Button>
                </DialogActions>
            </Dialog>
        );
    }



    render() {
        const { classes } = this.props;

        if (this.state.redirectGames) {
            return (
                <div>
                    {this.renderRedirectLogin()}
                    {this.renderRedirectProfile()}
                    {this.renderRedirectGames()}
                </div>
            )

        }

        if (!this.state.doneLoading) {
            return (
                <div>
                    <LoggedHeader user={global.user} cart={global.cart} heightChange={false} height={600} />

                    <div className="animated fadeOut animated" id="firstLoad" style={{ width: "100%", marginTop: "15%" }}>
                        <FadeIn>
                            <Lottie options={this.state.animationOptions} height={"20%"} width={"20%"} />
                        </FadeIn>
                    </div>
                </div>
            )
        } else {
            var auctionListings = <div></div>
            if (!this.state.loadingAuctions) {
                console.log(this.state.auctionsListings)
                if (this.state.auctionsListings === null || this.state.auctionsListings.length === 0 || Object.keys(this.state.auctionsListings).length == 0) {
                    auctionListings = <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                        <div style={{ textAlign: "left" }}>
                            <h3 style={{ color: "#999" }} id="emptyAuctionsMessage">
                                It seems like no auctions exists related to this game at the moment :(
                    </h3>
                        </div>
                    </GridItem>
                } else {
                    auctionListings = <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                        <TableContainer component={Paper}>
                            <Table style={{ width: "100%" }} aria-label="simple table" id="auctionsTable">
                                <TableBody>
                                    {Object.keys(this.state.auctionsListings).map((id) => (
                                        console.log(this.state.auctionsListings[id]),
                                        <TableRow hover key={id}>
                                            <TableCell align="left">
                                                <Link to={"/user/" + this.state.auctionsListings[id].auctioneer} style={{ color: "#ff3ea0" }} >
                                                    <b><i class="far fa-user"></i> {this.state.auctionsListings[id].auctioneer}</b>
                                                </Link>
                                            </TableCell>
                                            <TableCell align="left">{this.state.auctionsListings[id].score == -1 || this.state.auctionsListings[id].score == null ? "UNRATED" : <b>{this.state.auctionsListings[id].score}</b>} <b><i class="far fa-star"></i></b></TableCell>
                                            <TableCell align="center">Ends {this.state.auctionsListings[id].endDate}</TableCell>
                                            <TableCell align="center"><span
                                                style={{ color: "#ff3ea0"}}>Highest bidder {this.state.auctionsListings[id].buyerTxt}</span></TableCell>
                                            <TableCell align="right">
                                                <span style={{ color: "#f44336", fontSize: "25px", fontWeight: "bolder" }}>
                                                    {this.state.auctionsListings[id].price}€
                                                </span>
                                            </TableCell>
                                            <TableCell align="center">
                                                <Button
                                                    size="md"
                                                    id={id}
                                                    style={{ backgroundColor: "#4ec884" }}
                                                    onClick={(e) => {
                                                        this.setClassicModal(true, e);
                                                    }}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                >
                                                    <i class="fas fa-gavel"></i> Make a Bidding
                              </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </GridItem>
                }
            } else {
                auctionListings = <div
                    className="animated fadeOut animated"
                    id="loadingSales"
                    style={{
                        top: "50%",
                        left: "50%",
                        display: ""
                    }}>
                    <FadeIn>
                        <Lottie options={this.state.animationOptions} height={"20%"} width={"20%"} />
                    </FadeIn>
                </div>
            }

            var sellListings = <div></div>
            if (!this.state.loadingSell) {
                if (this.state.sellListings == null || this.state.sellListings.length == 0) {
                    sellListings = <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                        <div style={{ textAlign: "left" }}>
                            <h3 style={{ color: "#999" }} id="noSell">
                                It seems like no one's selling this game at the moment :(
                        </h3>
                        </div>
                    </GridItem>
                } else {
                    sellListings = [<GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                        <TableContainer component={Paper}>
                            <Table style={{ width: "100%" }} aria-label="simple table">
                                <TableBody>
                                    {this.state.sellListings.map((row) => (
                                        <TableRow hover key={row.name}>
                                            <TableCell align="left">
                                                <Link to={"/user/" + row.gameKey.retailer} style={{ color: "#ff3ea0" }} >
                                                    <b><i class="far fa-user"></i> {row.gameKey.retailer}</b>
                                                </Link>
                                            </TableCell>
                                            <TableCell align="left">{row.score == -1 || row.score == null ? "UNRATED" : <b>{row.score}</b>} <b><i class="far fa-star"></i></b></TableCell>
                                            <TableCell align="left"><b>{row.gameKey.platform}</b></TableCell>
                                            <TableCell align="right">
                                                <span style={{ color: "#f44336", fontSize: "25px", fontWeight: "bolder" }}>
                                                    {row.price}€
                                                </span>
                                            </TableCell>
                                            <TableCell align="right">
                                                {row.cart}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </GridItem>]

                    sellListings.push(<GridItem xs={12} sm={12} md={12} style={{ marginTop: "20px" }}>
                        <div style={{ margin: "auto", width: "40%" }}>
                            <Pagination count={this.state.noListingPages} page={this.state.listingsPage} onChange={this.changePageSales} variant="outlined" shape="rounded" />
                        </div>
                    </GridItem>)
                }
            } else {
                sellListings = <div
                    className="animated fadeOut animated"
                    id="loadingAuctions"
                    style={{
                        top: "50%",
                        left: "50%",
                        display: ""
                    }}>
                    <FadeIn>
                        <Lottie options={this.state.animationOptions} height={"20%"} width={"20%"} />
                    </FadeIn>
                </div>
            }

            var reviewListings = <div></div>
            if (!this.state.loadingReviews) {
                if (this.state.reviewListings == null || this.state.reviewListings.length == 0) {
                    reviewListings = <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                        <div style={{ textAlign: "left" }}>
                            <h3 style={{ color: "#999" }} id="noReviews">
                                It seems like no one's reviewed this game yet :(
                        </h3>
                        </div>
                    </GridItem>
                } else {
                    reviewListings = [<GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                        <TableContainer component={Paper}>
                            <Table style={{ width: "100%" }} aria-label="simple table">
                                <TableBody>
                                    {this.state.reviewListings.map((row) => (
                                        <TableRow hover key={row.name}>
                                            <TableCell align="left">
                                                <Link to={"/user/" + row.authorUsername} style={{ color: "#ff3ea0" }} >
                                                    <b><i class="far fa-user"></i> {row.authorUsername}</b>
                                                </Link>
                                            </TableCell>
                                            <TableCell align="left"><b>{row.score}</b></TableCell>
                                            <TableCell align="left"><b>{row.comment == "" ? <span style={{ color: "#999" }}><i>No Comment</i></span> : <span>"{row.comment}"</span>}</b></TableCell>
                                            <TableCell align="left">{row.date.split("T")[0]}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                    </GridItem>]

                    reviewListings.push(<GridItem xs={12} sm={12} md={12} style={{ marginTop: "20px" }}>
                        <div style={{ margin: "auto", width: "40%" }}>
                            <Pagination count={this.state.noReviewPages} page={this.state.reviewsPage} onChange={this.changePageReviews} variant="outlined" shape="rounded" />
                        </div>
                    </GridItem>)
                }
            } else {
                reviewListings = <div
                    className="animated fadeOut animated"
                    id="loadingAuctions"
                    style={{
                        top: "50%",
                        left: "50%",
                        display: ""
                    }}>
                    <FadeIn>
                        <Lottie options={this.state.animationOptions} height={"20%"} width={"20%"} />
                    </FadeIn>
                </div>
            }

            var gameHeader = null
            var gameInfo = null

            var bestPrice
            if (global.user != null) {
                bestPrice = <GridItem xs={12} sm={12} md={2}>
                    <div style={{ textAlign: "left", paddingTop: "15px" }}>
                        <Button
                            size="md"
                            style={{ backgroundColor: "#1598a7", width: "100%" }}
                            target="_blank"
                            rel="noopener noreferrer"
                            onClick={() => this.addToWishlist()}
                            id="wishlistButton"
                        >
                            <i class="far fa-heart"></i> Add to Wishlist
                    </Button>
                    </div>
                </GridItem>
            } else {
                bestPrice = <GridItem xs={12} sm={12} md={2}>
                    <div style={{ textAlign: "left", paddingTop: "15px" }}>
                        <Button
                            size="md"
                            style={{ backgroundColor: "#1598a7", width: "100%" }}
                            target="_blank"
                            rel="noopener noreferrer"
                            disabled
                            id="wishlistButton"
                        >
                            <i class="far fa-heart"></i> Add to Wishlist
                    </Button>
                    </div>
                </GridItem>
            }
            if (this.state.game.bestSell != null) {
                var score = <span style={{ color: "#999" }}>No one's reviewed this user yet!</span>

                if (this.state.game.bestSell.id == -1) {
                    score = <span style={{ color: "#999" }}>No one's reviewed this user yet!</span>
                }
                else if (this.state.game.bestSell.id > 0 && this.state.game.bestSell.id <= 1) {
                    score = <span style={{ color: "red" }}><b>{this.state.game.bestSell.id} <i class="far fa-star"></i></b></span>
                } else if (this.state.game.bestSell.id < 4) {
                    score = <span style={{ color: "#fc926e" }}><b>{this.state.game.bestSell.id} <i class="far fa-star"></i></b></span>
                } else if (this.state.game.bestSell.id <= 5) {
                    score = <span style={{ color: "#4ec884" }}><b>{this.state.game.bestSell.id} <i class="far fa-star"></i></b></span>
                }

                var button = null
                var inCart = false
                if (global.cart != null) {
                    for (var i = 0; i < global.cart.games.length; i++) {
                        var foundGame = global.cart.games[i]
                        if (this.state.game.bestSell.id == foundGame.id) {
                            inCart = true;
                            break
                        }
                    }
                }

                var wishlist = null
                if (global.user != null) {
                    if (!inCart) {
                        button = <Button
                            size="md"
                            style={{ backgroundColor: "#4ec884", width: "100%" }}
                            target="_blank"
                            rel="noopener noreferrer"
                            id="addToCartButtonBest"

                            onClick={() => this.addToCart(this.state.game.bestSell)}
                        >
                            <i class="fas fa-cart-arrow-down"></i> Add to Cart
                    </Button>
                    } else {
                        button = <Button
                            size="md"
                            style={{ backgroundColor: "#ff3ea0", width: "100%" }}
                            target="_blank"
                            rel="noopener noreferrer"
                            id="removeFromCartButtonBest"
                            onClick={() => this.removeFromCart(this.state.game.bestSell)}
                        >
                            <i class="fas fa-cart-arrow-down"></i> Remove from Cart
                    </Button>
                    }

                    wishlist = <Button
                        size="md"
                        style={{ backgroundColor: "#1598a7", width: "100%" }}
                        target="_blank"
                        rel="noopener noreferrer"
                        id="wishlistButton"
                        onClick={() => this.addToWishlist()}
                    >
                        <i class="far fa-heart"></i> Add to Wishlist
                    </Button>
                } else {
                    if (!inCart) {
                        button = <Button
                            size="md"
                            style={{ backgroundColor: "#4ec884", width: "100%" }}
                            target="_blank"
                            rel="noopener noreferrer"
                            id="addToCartButtonBest"
                            disabled
                        >
                            <i class="fas fa-cart-arrow-down"></i> Add to Cart
                    </Button>
                    } else {
                        button = <Button
                            size="md"
                            style={{ backgroundColor: "#ff3ea0", width: "100%" }}
                            target="_blank"
                            rel="noopener noreferrer"
                            id="removeFromCartButtonBest"
                            disabled
                        >
                            <i class="fas fa-cart-arrow-down"></i> Remove from Cart
                    </Button>
                    }

                    wishlist = <Button
                        size="md"
                        style={{ backgroundColor: "#1598a7", width: "100%" }}
                        target="_blank"
                        rel="noopener noreferrer"
                        disabled
                        id="wishlistButton"
                    >
                        <i class="far fa-heart"></i> Add to Wishlist
                    </Button>
                }


                bestPrice = <GridItem xs={12} sm={12} md={2}>
                    <div style={{ textAlign: "left", marginTop: "30px" }}>
                        <span style={{ color: "#999", fontSize: "12px" }} id="bestOfferSign">
                            BEST OFFER
                            </span>
                    </div>
                    <div style={{ textAlign: "left" }}>
                        <span style={{ color: "#3b3e48", fontSize: "15px", fontWeight: "bolder" }}>
                            {this.state.game.bestSell.gameKey.retailer}
                        </span>
                    </div>
                    <div style={{ textAlign: "left" }}>
                        {score}
                        <span style={{ color: "#999", fontSize: "15px", fontWeight: "bolder", marginLeft: "10px" }}>
                            Grid Score
                            </span>
                    </div>
                    <div style={{ textAlign: "left" }}>
                        <Button
                            color="danger"
                            size="sm"
                            style={{ backgroundColor: "#ff3ea0" }}
                            onClick={() => this.goToProfile(this.state.game.bestSell.gameKey.retailer)}
                            target="_blank"
                            rel="noopener noreferrer"
                        >
                            <i class="far fa-user"></i> Seller Profile
                            </Button>
                    </div>

                    <div style={{ textAlign: "left", marginTop: "20px" }}>
                        <span style={{ color: "#999", fontSize: "12px" }}>
                            Price
                            </span>
                    </div>
                    <div style={{ textAlign: "left" }}>
                        <span style={{ color: "#f44336", fontSize: "40px", fontWeight: "bolder" }} id="bestOfferPrice">
                            {this.state.game.bestSell.price}€
                            </span>

                    </div>
                    <div style={{ textAlign: "left" }}>
                        <span style={{ color: "#3b3e48", fontSize: "18", fontWeight: "bolder" }}>
                            ({this.state.game.bestSell.gameKey.platform} Key)
                            </span>
                    </div>

                    <div style={{ textAlign: "left", marginTop: "5px" }}>
                        {button}
                        {wishlist}
                    </div>
                </GridItem>
            }

            if (this.state.game != null) {
                var score = <span style={{ color: "#999" }}>UNRATED</span>

                if (this.state.game.score == -1) {
                    score = <span style={{ color: "#999" }}>UNRATED</span>
                }
                else if (this.state.game.score > 0 && this.state.game.score <= 1) {
                    score = <span style={{ color: "red" }}><b>{this.state.game.score} <i class="far fa-star"></i></b></span>
                } else if (this.state.game.score < 4) {
                    score = <span style={{ color: "#fc926e" }}><b>{this.state.game.score} <i class="far fa-star"></i></b></span>
                } else if (this.state.game.score <= 5) {
                    score = <span style={{ color: "#4ec884" }}><b>{this.state.game.score} <i class="far fa-star"></i></b></span>
                }

                gameHeader = <div style={{ padding: "70px 0" }}>
                    <GridContainer>
                        <GridItem xs={12} sm={12} md={5}>
                            <img
                                src={this.state.game.coverUrl}
                                alt="..."
                                style={{ width: "95%", height: "260px", marginTop: "28px" }}
                                className={
                                    classes.imgRaised +
                                    " " +
                                    classes.imgRounded
                                }
                            />
                        </GridItem>

                        <GridItem xs={12} sm={12} md={5}>
                            <div style={{ textAlign: "left" }}>
                                <h3 style={{ color: "#3b3e48", fontWeight: "bolder" }}><b style={{ color: "#3b3e48" }} id="gameNameHeader">{this.state.game.name}</b></h3>
                                <hr style={{ color: "#999" }}></hr>
                            </div>
                            <div style={{ textAlign: "left", marginTop: "30px" }}>
                                <span style={{ color: "#999", fontSize: "15px" }}>
                                    <b>Description:</b> <span style={{ color: "#3b3e48" }} id="descriptionHeader"> {this.state.game.minimizedDescription}</span>
                                </span>
                            </div>
                            <div style={{ textAlign: "left", marginTop: "30px" }}>
                                <span style={{ color: "#999", fontSize: "25px" }}>
                                    <img src={image} style={{ marginBottom: "10px" }}></img><b> Grid Score:</b> {score}
                                </span>
                            </div>
                        </GridItem>

                        {bestPrice}
                    </GridContainer>
                </div>

                gameInfo = [
                    <div className={"search"} style={{ padding: "45px 0px" }}>
                        <GridContainer>
                            <GridItem xs={12} sm={12} md={12}>
                                <span>
                                    <h2 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Game Details
                            </h2>
                                </span>
                            </GridItem>
                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #fdf147" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Name
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }} id="fullName">
                                            {this.state.game.name}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #feec4c" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Release Date
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }} id="fullReleaseDate">
                                            {this.state.game.releaseDate}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #f5c758" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Description
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "15px" }} id="fullDescription">
                                            {this.state.game.description}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #fca963" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Genres
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }} id="fullGenres">
                                            {this.state.game.allGenres}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #f77a71" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Platforms
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }} id="fullPlatforms">
                                            {this.state.game.allPlatforms == "" ? <span style={{ color: "#999" }}><i>This game isn't currently available on any platforms</i></span> : <span>{this.state.game.allPlatforms}</span>}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #fc4b8f" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Developer
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }} id="fullDevelopers">
                                            {this.state.game.allDevelopers}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} style={{ borderRight: "2px solid #fc1bbe" }}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Publisher
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }} id="fullPublisher">
                                            {this.state.game.publisher.name}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>
                        </GridContainer>
                    </div>,
                    <div className={"searchMobile"} style={{ padding: "45px 0px" }}>
                        <GridContainer>
                            <GridItem xs={12} sm={12} md={12}>
                                <span>
                                    <h2 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>Game Details
                            </h2>
                                </span>
                            </GridItem>
                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "15px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} >
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #fdf147" }}>Name
                                            </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }}>
                                            {this.state.game.name}
                                        </div>
                                    </GridItem>
                                </GridContainer>

                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} >
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #feec4c" }}>Release Date
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }}>
                                            {this.state.game.releaseDate}
                                        </div>
                                    </GridItem>
                                </GridContainer>

                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #f5c758" }}>Description
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "15px" }}>
                                            {this.state.game.description}
                                        </div>
                                    </GridItem>
                                </GridContainer>

                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} >
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #fca963" }}>Genres
                                            </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }}>
                                            {this.state.game.allGenres}
                                        </div>
                                    </GridItem>
                                </GridContainer>

                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2} >
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #f77a71" }}>Platforms
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }}>
                                            {this.state.game.allPlatforms == "" ? <span style={{ color: "#999" }}><i>This game isn't currently available on any platforms</i></span> : <span>{this.state.game.allPlatforms}</span>}
                                        </div>
                                    </GridItem>
                                </GridContainer>

                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={2}>
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #fc4b8f" }}>Developer
                                    </h3>
                                        </span>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={1} style={{ marginTop: "10px", height: "100%" }}>
                                    </GridItem>
                                    <GridItem xs={12} sm={12} md={9} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }}>
                                            {this.state.game.allDevelopers}
                                        </div>
                                    </GridItem>
                                </GridContainer>

                            </GridItem>

                            <GridItem xs={12} sm={12} md={12} style={{ marginTop: "15px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={12} >
                                        <span>
                                            <h3 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0", borderBottom: "2px solid #fc1bbe" }}>Publisher
                                    </h3>
                                        </span>
                                    </GridItem>

                                    <GridItem xs={12} sm={12} md={12} style={{ marginTop: "10px" }}>
                                        <div style={{ color: "black", fontSize: "18px" }}>
                                            {this.state.game.publisher.name}
                                        </div>
                                    </GridItem>
                                </GridContainer>
                            </GridItem>
                        </GridContainer>
                    </div>
                ]
            }

            return (
                <div>
                    <LoggedHeader user={global.user} cart={global.cart} heightChange={false} height={600} />
                    {this.renderRedirectLogin()}
                    {this.renderRedirectProfile()}
                    {this.renderRedirectGames()}

                    <ToastContainer
                        position="top-center"
                        autoClose={2500}
                        hideProgressBar={false}
                        transition={Flip}
                        newestOnTop={false}
                        closeOnClick
                        rtl={false}
                        pauseOnVisibilityChange
                        draggable
                        pauseOnHover
                    />


                    <div className={classNames(classes.main)} style={{ marginTop: "60px" }}>
                        <div className={classes.container}>

                            {gameHeader}

                            <div style={{ padding: "20px 0px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={12}>
                                        <span>
                                            <h2 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>{this.state.noSells} Offers
                                            </h2>
                                        </span>
                                    </GridItem>
                                    {sellListings}

                                </GridContainer>
                            </div>

                            <div style={{ padding: "45px 0px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={12}>
                                        <span>
                                            <h2 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>{this.state.auctionsListings != null ? Object.keys(this.state.auctionsListings).length : 0} Auctions
                                            </h2>
                                        </span>
                                    </GridItem>
                                    {auctionListings}
                                </GridContainer>
                            </div>

                            <div style={{ padding: "45px 0px" }}>
                                <GridContainer>
                                    <GridItem xs={12} sm={12} md={12}>
                                        <span>
                                            <h2 style={{ color: "#999", fontWeight: "bolder", marginTop: "0px", padding: "0 0" }}>{this.state.noReviews} Reviews
                                            </h2>
                                        </span>
                                    </GridItem>
                                    {reviewListings}

                                </GridContainer>
                            </div>

                            {gameInfo}
                        </div>

                        <Footer rawg={true} />

                    </div>
                    {this.renderModal()}
                </div>
            )
        }


    }
}

export default withStyles(Object.assign({}, stylesbasic, Object.assign({}, styles, stylesjs)))(Game);