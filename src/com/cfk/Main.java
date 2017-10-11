package com.cfk;

import java.util.Random;


public class Main {

    public static void main(String[] args) {
	// write your code here
    }

    private class NPCharacter {

    }
}

/**
 * alg:
 * set of x characters
 *  set of properties
 *      race
 *          human, halfling, orc, elf
 *      occupation
 *          craft/profession skills
 *      social class
 *          poor/working/middle(civil/merchant/military)/upper/noble/burgher
 *      age
 *          youngish, middle-aged, old
 *      sex
 *      quirks/traits
 *          handedness
 *          hair color
 *          height/weight
 *          smoker? good/bad eyesight?
 *
 *  procedurally generate the above
 *     enum for each property, get random option
 *
 *  select one character
 *      select n properties to leave clues
 *      generate clues
 *
 *  set of method types
 *      stab/slash/crush; burn/poison/shock/melt/freeze/other magic
 *      select one
 *      generate clues
 *      generate weapon
 *
 *  add clues to room
 *  add other random clues to room
 *
 *  nice to have:
 *      diff clues need diff skills to find
 *      each skill within a clue has a set of levels of detail for better check results
 *      occupations have schedules
 *          day/swing/graveyard
 *          what characters were seen by whom: rule out suspects
 *          what was heard by whom: leads on methods
 *
 *  because the perp was mind-controlled, no motive + finding weapon is proof
 */


/*
Profession: Merchant: Mercer (general stores)         Guild:        Mercers #1
Profession: Merchant: Grocer (sells vegtables)        Guild:        Grocer #2
Profession: Merchant: Clothier                        Guild:        Drapers #3
Profession: Merchant: Fishmonger                      Guild:        Fishmongers #4
Craft: Gemcutter/Goldsmith                            Guild:        Goldsmiths #5
Profession: Assayer                                   Guild:        Goldsmiths #5
Craft: Skinner                                        Guild:        Skinners #6
Craft: Tailor                                         Guild:        Merchant Tailors #7
Profession: Merchant: Clothier                        Guild:        Haberdashers #8
Craft: Salter                                         Guild:        Salters #9
Craft: Blacksmith                                     Guild:        Ironmongers #10
Craft: Vintner                                        Guild:        Vintners #11
Profession: Merchant: Clothier                        Guild:        Clothiers #12


Craft: Apocathery (Herbalist)                         Guild:        Apothecaries
Craft: Armorer                                        Guild:        Armourers & Braziers
Craft: Baker                                          Guild:        Bakers
Craft: Basketweaver (baskets & wicker items)          Guild:        Basketweavers
Craft: Bookbinder (creates books)                     Guild:        Stationers
Craft: Bookseller (copies books)                      Guild:        Stationers
Craft: Bowyer                                         Guild:        Bowyers
Craft: Brewer                                         Guild:        Brewers
Craft: Broderer (embroidery)                          Guild:        Broderers
Craft: Butcher                                        Guild:        Butchers
Craft: Card Maker (playing cards)                     Guild:        Playing Card Makers
Craft: Carpenter (wood furniture with nails)          Guild:        Carpenters
Craft: Cartwright                                     Guild:        Cartwrights/Wainwrights
Craft: Clockmaker                                     Guild:        Clockmakers
Craft: Cobbler (shoes)                                Guild:        Cobblers
Craft: Constructor (buildings)                        Guild:        Constructors
Craft: Cook                                           Guild:        Cooks
Craft: Cooper (barrels)                               Guild:        Coopers
Craft: Cordwainer (Leatherworking)                    Guild:        Cordwainers
Craft: Cutler (guild) (bladed tools)                  Guild:        Cutlers
Craft: Distiller (Guild: Vintner)                     Guild:        Distillers
Craft: Dyer (both making dye and applying it)         Guild:        Dyers
Craft: Fans                                           Guild:        Fan Makers
Craft: Fletcher (guild)                               Guild:        Fletchers
Craft: Founder (brass & bronze items)                 Guild:        Armourers & Braziers
Craft: Fuller (guild) (making cloth from woven wool)  Guild:        Fuller
Craft: Furrier                                        Guild:        Furriers
Craft: Girdler (belts & girdles)                      Guild:        Girdlers
Craft: Glassblower                                    Guild:        Glass Sellers
Craft: Glazier (glass window making & staining)       Guild:        Glaziers
Craft: Glover                                         Guild:        Glovers
Craft: Hatmaker                                       Guild:        Felters
Craft: Horner (leather & horn bottles)                Guild:        Horners
Craft: Illuminator (Illuminates books)                Guild:        Stationers
Craft: Joiners (wood furnature with glue)             Guild:        Joiners
Craft: Knitter                                        Guild:        Knitters
Craft: Locksmith                                      Guild:        Locksmiths
Craft: Loriner (metal parts of saddles etc)           Guild:        Loriners
Craft: Mason                                          Guild:        Masons
Craft: Miller                                         Guild:        Millers
Craft: Needlemaker                                    Guild:        Needlemakers
Craft: Oculist (spectacles, crystal balls, prisms)    Guild:        Spectacle Makers
Craft: Paper & Ink                                    Guild:        Stationers
Craft: Pewterer                                       Guild:        Pewterers
Craft: Saddler                                        Guild:        Saddlers
Craft: Shipwright                                     Guild:        Shipwrights
Craft: Wire Drawers (making wire for embroidery)      Guild:        Silver & Gold Wire Drawers
Craft: Spinner                                        Guild:        Woolmen
Craft: Tallow Chandler (tallow candles & soap)        Guild:        Tallow Chandlers
Craft: Tiler & Bricklayer                             Guild:        Tilers & Bricklayers
Craft: Tobacconist (making pipes & blending tobacco)  Guild:        Pipe Makers & Tobacco Blenders
Craft: Turner (lathe woodwork)                        Guild:        Turners
Craft: Upholder (upholstery & matresses)              Guild:        Upholders
Craft: Wax Chandler (wax candles)                     Guild:        Wax Chandlers
Profession: Barber                                    Guild:        Barbers
Profession: Car Man (cart operator)                   Guild:        Car Man
Profession: Carriage Driver                           Guild:        Carriage Drivers
Profession: Clerk                                     Guild:        Parrish Clerks
Profession: Ferrier (horse shoes)                     Guild:        Ferriers
Profession: Gardener (vegetable farming)              Guild:        Gardeners
Profession: Innkeeper                                 Guild:        Innholders
Profession: Launderer                                 Guild:        Launderers
Profession: Merchant: Fruiterer (sells fruit)         Guild:        Fruiterers
Profession: Merchant: Trader                          Guild:        Trader
Profession: Messenger                                 Guild:        Messengers
Profession: Musician                                  Guild:        Musicians
Profession: Painter-Stainer (paints & stains wood)    Guild:        Painter-Stainers
Profession: Pattenmaker (wooden clog shoes)           Guild:        Pattenmakers
Profession: Pavier (roads)                            Guild:        Paviers
Profession: Plasterer                                 Guild:        Plasterers
Profession: Plumber                                   Guild:        Plumbers
Profession: Poltier (keeper of poultry)               Guild:        Poltier
Profession: Scribe (Notary)                           Guild:        Scriveners
Profession: Teamster (unskilled labor)                Guild:        Lightners


Craft: Alchemist
Craft: Musical Instruments
Craft: Perfumes
Craft: Pottery
Craft: Rope/Netmaker
Craft: Tanner
Craft: Thatcher (thatch roofs)
Craft: Tinker (repairs tools such as pots, blades)
Craft: Weapon Smith
Craft: Weaver (tapestries, rugs, cloth)
Craft: Wireworker (anything with wire: fishhooks, cages)
Profession: Acrobat
Profession: Adventurer
Profession: Animal Breeder
Profession: Animal Handler
Profession: Animal Tamer
Profession: Arbitrator
Profession: Architect
Profession: Artist
Profession: Barrister
Profession: Caravaneer
Profession: Cartographer
Profession: Chimney Sweep
Profession: Chirurgeon
Profession: Counsellor (Wise Man)
Profession: Courtier/Courtesan
Profession: Dancer
Profession: Dentist
Profession: Drill Instructor (trains in the use of weapons)
Profession: Entertainer (Actor/Singer)
Profession: Fisherman
Profession: Forrester
Profession: Fortune Teller
Profession: Fruiterer (fruit & nut farming)
Profession: Gambler
Profession: Grain Farmer
Profession: Grave Digger
Profession: Groom
Profession: Guard (private or police)
Profession: Guide
Profession: Herald
Profession: Herder
Profession: Hunter
Profession: Interpreter
Profession: Juggler
Profession: Landlord
Profession: Logger
Profession: Magician
Profession: Money Lender
Profession: Navigator
Profession: Noble
Profession: Priest
Profession: Prize fighter
Profession: Rancher
Profession: Rat Catcher
Profession: Sage
Profession: Server (Barteneder, serving wench)
Profession: Shearer
Profession: Slaver
Profession: Spy
Profession: Stable Hand
Profession: Tavernkeeper
Profession: Trapper
Profession: Tutor
Profession: Veterinarian

Profession: Soldier: Archery
Profession: Soldier: Calvalry
Profession: Soldier: Engineer (Craft: War Machines)
Profession: Soldier: Infantry
Profession: Soldier: Navy
Profession: Soldier: Officer

Profession: Underworld/Merchant: Fence (Pawn)
Profession: Underworld: Assassin
Profession: Underworld: Beggar
Profession: Underworld: Burglar
Profession: Underworld: Con Man (Snake Oil Salesman)
Profession: Underworld: Pirate
Profession: Underworld: Robber (Bandit, Armed Robber)
Profession: Underworld: Thug (Organized crime - Protection money, Gambling, etc)

 */