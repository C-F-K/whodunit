package com.cfk.whodunit;

import java.util.*;

public class Main {
    private static HashMap<String,Command> allowedCmds = new HashMap<String,Command>(){{
        this.put("examine", new Examine());
        this.put("exit", new Exit());
        this.put("generate", new Generate());
        this.put("help", new Help());
        this.put("list", new ListAssets());
        this.put("search", new Search());
    }};

    private static CrimeScene scene;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String cmd;
        String[] params;

        while(true) {
            System.out.print("> ");
            params = s.nextLine().toLowerCase().split(" ");
            cmd = params[0];
            if (allowedCmds.containsKey(cmd)) {
                allowedCmds.get(cmd).doCommand(Arrays.copyOfRange(params,1,params.length));
            }
        }
    }

    private interface Command {
        void doCommand(String... params);
        String getHelpText();
    }

    private static class Examine implements Command {
        @Override
        public void doCommand(String... params) {
            if (scene == null) {
                System.out.println("No CrimeScene; do 'generate' first");
                return;
            }
        }
        @Override
        public String getHelpText() {
            return "Examine a clue";
        }
    }

    private static class Exit implements Command {
        @Override
        public void doCommand(String... params) {
            System.exit(0);
        }
        @Override
        public String getHelpText() {
            return "Exit to command line";
        }
    }

    private static class Generate implements Command {
        @Override
        public void doCommand(String... params) {

        }
        @Override
        public String getHelpText() {
            return "Generate a new crime scene";
        }
    }

    private static class Help implements Command {
        @Override
        public void doCommand(String... params) {
            for (String s : allowedCmds.keySet()) {
                System.out.println(s + " - " + allowedCmds.get(s).getHelpText());
            }
        }
        @Override
        public String getHelpText() {
            return "Display this text";
        }
    }

    private static class ListAssets implements Command {
        @Override
        public void doCommand(String... params) {
            if (scene == null) {
                System.out.println("No CrimeScene; do 'generate' first");
                return;
            }
            if (params.length == 0) {
                System.out.println("Needs additional parameter - 'suspects' or 'clues'");
                return;
            }
            switch (params[0]) {
                case "suspects":
                    break;
                case "clues":
//					for (Object clue : scene.getAllClues().stream().filter(Clue::isFound).toArray()) {
//						Clue c = (Clue) clue;
//						System.out.println(c.getDescription());
//					}
                    for (Clue clue : scene.getAllClues()) {
                        if (clue.isFound()) {
                            System.out.println(clue.getDescription());
                        }
                    }
                    break;
                default:
                    System.out.println("Asset type unrecognized");
            }
        }
        @Override
        public String getHelpText() {
            return "List assets (suspects or found clues)";
        }
    }

    private static class Search implements Command {
        @Override
        public void doCommand(String... params) {
            if (scene == null) {
                System.out.println("No CrimeScene; do 'generate' first");
                return;
            }
            if (params.length == 0) {
                System.out.println("Needs additional parameter - Search check result");
                return;
            }
            int result;
            try {
                result = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                System.out.println("Search check result not formatted correctly - needs whole number");
                return;
            }
            boolean foundClues = false;
            for (Clue clue : scene.getAllClues()) {
                if (result >= clue.getCheckDC()) {
                    if (!foundClues) {
                        foundClues = true;
                    }
                    clue.setFound(true);
                    System.out.println("Found new clue: " + clue.getDescription());
                }
            }
            if (!foundClues) {
                System.out.println("No new clues found");
            }
        }
        @Override
        public String getHelpText() {
            return "Search the scene for new clues";
        }
    }

    private class CrimeScene {
        private ArrayList<Clue> allClues;
        public ArrayList<Clue> getAllClues() {
            return allClues;
        }
        public void setAllClues(ArrayList<Clue> allClues) {
            this.allClues = allClues;
        }
    }

    private class Clue {
        private boolean found = false;
        private final String description;
        private final int checkDC;

        public Clue(String desc, int checkDC) {
            this.description = desc;
            this.checkDC = checkDC;
        }
        public boolean isFound() {
            return found;
        }
        public void setFound(boolean found) {
            this.found = found;
        }
        public String getDescription() {
            return description;
        }
        public int getCheckDC() {
            return checkDC;
        }
    }

    private class NPCharacter {

    }

    private final class Killer extends NPCharacter {

    }

    private final class Innocent extends NPCharacter {

    }

    private interface ClueDropper {
        Clue getClue();
    }

    private enum Race implements ClueDropper {
        HUMAN, HALFLING, ORC, ELF;
    }

    private enum Age implements ClueDropper {

    }

    private enum SocialClass implements ClueDropper {

    }

    private enum Occupation implements ClueDropper {

    }

    private enum Quirk implements ClueDropper {

    }
}

/**
 * alg:
 * set of x characters
 *  set of properties
 *      race
 *          human, halfling, orc, elf
 *      occupation
 *          craft/profession skills (see below)
 *      social class
 *          poor/working/middle(civil/merchant/military)/upper(socialite/merchant)/noble/burgher
 *      age
 *          youngish, middle-aged, old
 *      sex
 *      traits
 *          handedness
 *          hair color
 *          height/weight
 *      quirks
 *          see below
 *
 *  procedurally generate the above
 *     enum for each property, get random option
 *
 *  select one character (actually: generate one killer and several innocents)
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
 *      avoid: being able to brute force which is the killer
 *          because some clues don't refer to anyone
 *          and all the ones that do refer to the killer
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

/* Quirks 1-100

Scar
Monocle
Gaps between teeth
Rash
Tattoo
Missing limb, joints, fingers, teeth
Spits when talking
Fidgets
Picks ears
Rubs hands together
Sucks on teeth
Mismatched eyes
Acne
Overweight
Underweight
Always eating or drinking
Bites fingernails
Wandering eye
Facial piercing
Limp
Lisp
Foreign clothing
Accent
Uses long words incorrectly
Rubs chin
Tugs on ear
Picks nose
Bites lower lip
Trims or cleans nails with long knife
Twirls hair with finger
Single hair or beard braid
Many hair or beard braids
Rings or bells in hair or beard braids
Smells like sweat
Smells like dogs
Smells like horses
Smells like cheap cologne
Smells like expensive cologne
Smells like cedar wood
Smells like tobacco
Smells like lavender
Smells like alcohol
Smells like soap
Smells like roses
Scratches
Has leaves in hair or clothing
Wears flower in hair or clothing
Squints
Sniffs often
Braggart
Never looks anyone in the eye
Superstitious
Unusual jewelry
Freckles
Pockmarked skin
Eye tic
Bucktoothed
Talks loudly
Whispers
Holier than thou attitude
Makes puns
Clumsy
Quotes famous poet, regularly
Quotes religious text
Refers to self in third person
Chews grass, stick, or leaves
Closely examines everything
Keeps hand on weapon
Keeps hands in pockets
Hairless
Exotic weapon
Claps hands often
Smacks palm with fist
Asks often about his own appearance
Rubs palms on thighs
Stretches often
Seems cold or hot
Yawns often
Polishes brooch or buckle often
Rubs luckstone or fetish
Twirls coin between fingers
Always wears thick, leather gloves
High squeaky voice
Offers exotic beverages or foods
Oily skin or hair
Obvious cosmetics
Lazy eye
Always wears ‘lucky’ hat, scarf, ring
Mole
Always wears favorite color
Wears clothes too big or too small
Wears obvious wig
Uses a parasol
Picks teeth with knife or fingernails
Waxed facial hair
Belches loud and often
Sneezes often
Coughs regularly
Blinks often
(roll twice)

Quirks 101-200

Wine colored birthmark
Rubs eyes often
Chews on braid or lock of hair
Bites fingernails or cuticles
Polishes spectacles, never wears them
Twists ring
Picks lint off clothes, his and PCs
Has hiccups
Watering eye(s)
Taps foot or drums fingers
Sunburn and peeling skin
Dark tan
Very pale
Toothless
Dressed in furs or leather
Dressed in formal attire
Dressed in silks and lace
Has black eye
Bruised extremities
Arm in sling
Bandaged extremity
Stutters
Tongue tied
Mixes languages
Mixes gibberish with words
Keeps arms folded
Picks at scabs or loose skin
Dresses colorblind
Has wart on hand, covers it always
Hand covers mouth while speaking
Keeps hand on PCs shoulder
Carries crumbs to feed birds
Hands coppers to beggar children
Speaks to animals like people
Speaks to ‘spirits’
Has paint stained hands and clothes
Wears scarf around ankles or wrists
Wears vest with bell buttons
Fascinated by fire
Fascinated by non human races
Clothes have bulging pouches
Archaic speech
Spotless, glossy boots
Rope burns around neck or wrists
Pasted on moustache or beard
Hair dye covering collar
Writes down everything PC says
Wears armor covered in runes
Has vampire bite scars on neck
Has arthritis in hands
Rubs old knee injury
Avoids crowds
Nose bleeds
Wears notched belt
Wears well tended ancient weapon
Refers to good old days often
Wears heart shaped beauty mark
Has one or more gold teeth
Ogles opposite sex openly
Paper cuts and ink stained fingers
Serious burn scar
Rings sewn on clothes with fetishes
Clothes made of woven leaves
Pregnant woman
Rubs calluses on palms with thumb
Carries cloth covered basket
Sharpens knife with whetstone
Spit polishes thick, fancy bracelet
Hard of hearing
Gives a different name every meeting
Regularly blows stray hair out of eyes
Bows before speaking and after
Flatulent
Hooks thumbs in belt and bounces
Walks like a sailor
Crumbs in beard and clothes
Food stained face
Wine stained ‘moustache’
Carries small snake, mouse, or lizard
Wears foreign style clothes
Has long, lacquered nails
Mute, speaks through sign language
Blind, sees through familiar’s eyes
Uses foul language
Runny nose
Always late
Instructs PCs on latest fashion
Bad cook, begs PCs to eat
Rubs large belly often
Blatantly pouts
Calls PCs by wrong names
Convinced PCs need reforming
Giggles, inappropriately
Wears ancient fashions
Combs hair with fingers
Jingles coins in pocket or pouch
Intoxicated or acting
Talks in rhyme
Plays with charm on necklace
(roll twice)

Quirks 201-230

Says “hmm – hmm” often
Asks rhetorical questions and answers
Denies gossiping and proceeds to gossip
Falsely claims he is an adventurer too
Greatly exaggerates
Nods often
Gestures wildly with hands
Carries satchel filled with junk
Hacking cough
Sweats profusely
Shaving cuts on face
Military manner
Animal bite or claw scar
Sleepy
Always out of breath
Sketches PC while talking
Flips key ring with finger
Covered with road dust
Rolls “R’s” while speaking
Snorts while laughing
Complains of heartburn
Talks of health problems
Loses items and asks PCs to help look
Survived torture, has no fingernails
Vegetarian
Only eats meat
Conceited
Pathological liar
Taps foot incessantly
Rubs back of neck

* */