package com.cfk.whodunit;

import java.util.*;

public class Main {
    private static Random rng;
    private static final HashMap<String, HashMap<Integer, String>> EMPTY_HASH = new HashMap<>();

    private static HashMap<String,Command> allowedCmds = new HashMap<>(){{
        // system commands
        this.put("help", new Help());
        this.put("exit", new Exit());
        this.put("generate", new Generate());
        // generic actions
        this.put("examine", new Examine());                         // learn more about a clue
        this.put("talk", new Talk());                               // like examine but for people
        this.put("list", new ListAssets());
        // d&d skill checks
        this.put("diplomacy", new Diplomacy());                     // like getting a better result on clue skill check
        this.put("gather-information", new GatherInformation());    // like search but for people
        this.put("search", new Search());                           // find more clues
    }};

    private static CrimeScene scene;

    public static void main(String[] args) {
        try {
            rng = new Random(Long.parseLong(args[0]));
        } catch (NumberFormatException e) {
            rng = new Random(System.currentTimeMillis());
        }

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
            // examine stuph
            if (params.length == 0) {
                System.out.println("Examine what?");
                return;
            }
            // stuph = corpse
            if (params[0].matches("(victim|corpse|body)")) {
                String article = scene.getVictim().getAge().getDescription().charAt(0) == 'e' ? "n " : " ";
                System.out.println("You examine the victim's corpse...");
                System.out.println("The victim is a" +
                        article +
                        scene.getVictim().getAge().getDescription() +
                        " " + scene.getVictim().getRace().name().toLowerCase() +
                        " " + scene.getVictim().getJob().name().toLowerCase() + ".");
                // mundane examine
                if (scene.getVictim().getMethod().isElemental() || !scene.getVictim().getMethod().isMagic()) {
                    System.out.println(capitalize(scene.getVictim().getMethod().getDescription()) + ".");
                } else {
                    System.out.println("Nothing else makes itself apparent to the eye.");
                }
                if (params.length > 1 && params[1].matches("with")) {
                    if (params.length > 2 && params[2].matches("detect-magic")) {
                        // using detect magic
                        if (scene.getVictim().getMethod().isMagic()) { //
                            if (scene.getVictim().getMethod().isElemental()) {
                                System.out.println(capitalize(MurderMethod.EVOCATION.getDescription()) + ".");
                            } else {
                                System.out.println(capitalize(scene.getVictim().getMethod().getDescription()) + ".");
                            }
                        }
                    }
                }
                // stuph = a clue
            } else if (params[0].matches("clue")) {
                try {
                    if (params.length > 1 && scene.getClueIds().contains(Integer.parseInt(params[1]))) {
                        Clue clue = scene.getClueById(Integer.parseInt(params[1]));
                        if (clue != null && params.length > 2 && params[2].equals("with")) {
                            if (params.length > 3 && clue.getSkills().keySet().contains(params[3])) {
                                try {
                                    if (params.length > 4) {
                                        System.out.print(clue.getInfoAtOrBelowCheck(params[3], Integer.parseInt(params[4])));
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Skill check result is badly formatted");
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Clue ID doesn't exist or is badly formatted");
                }
            }
        }
        @Override
        public String getHelpText() {
            return "Examine elements of the crime scene";
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
            if (scene != null) {
                Scanner s = new Scanner(System.in);
                System.out.println("A CrimeScene already exists. Generating a new one will overwrite it and erase all your progress. Continue? y/N");
                System.out.print("> ");
                if (!s.nextLine().toLowerCase().trim().equals("y")) {
                    return;
                }
            }
            // do generate
            scene = new CrimeScene();
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

    private static class Talk implements Command {
        //        @Override
//        public void doCommand(String... params) {
//
//        }
        @Override
        public String getHelpText() {
            return "Talk to a suspect";
        }
    }

    private static class Diplomacy implements Command {
        //        @Override
//        public void doCommand(String... params) {
//
//        }
        @Override
        public String getHelpText() {
            return "Improve a character's attitude towards you";
        }
    }

    private static class GatherInformation implements Command {
        //        @Override
//        public void doCommand(String... params) {
//
//        }
        @Override
        public String getHelpText() {
            return "Locate new suspects or sources of information";
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
                clue.find(result);
                if (clue.isFound()) {
                    if (!foundClues) {
                        foundClues = true;
                    }
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

    private static class CrimeScene {
        private Corpse victim;
        private ArrayList<NPCharacter> suspects;
        private ArrayList<Clue> allClues;

        CrimeScene(){
            this.victim = new Corpse();
            NPCharacter killer = new NPCharacter(new MurderWeapon(victim.getMethod()));
            this.suspects = new ArrayList<>(){{
                this.add(killer);
                for (int i = 0; i < 9; i++) {
                    this.add(new NPCharacter());
                }
            }};
            this.allClues = new ArrayList<>(){{
                this.add(new Clue(killer.getRace().getFootprintSize() + " footprints",10, EMPTY_HASH));
                // add all killer clues
                // select between x and y suspects and add between p and q clues from each
            }};
            Collections.shuffle(suspects);
        }

        public Corpse getVictim() {
            return victim;
        }
        public ArrayList<NPCharacter> getSuspects() {
            return suspects;
        }
        public ArrayList<Clue> getAllClues() {
            return allClues;
        }
        public ArrayList<Integer> getClueIds() {
            return new ArrayList<>(){{
                for (Clue c : allClues) {
                    this.add(c.getId());
                }
            }};
        }
        public Clue getClueById(int id) {
            for (Clue c : allClues) {
                if (c.getId() == id) {
                    return c;
                }
            }
            return null;
        }
    }

    private static class Clue {
        private static int globalID = 0;
        // some fuckery is needed here
        private final int id;
        private boolean found = false;
        private final String description;
        private final int searchDC;
        private final HashMap<String, HashMap<Integer, String>> skills;

        public Clue(String desc, int checkDC, HashMap<String, HashMap<Integer, String>> skills) {
            this.id = ++globalID;
            this.description = desc;
            this.searchDC = checkDC;
            this.skills = skills;
        }
        public boolean isFound() {
            return found;
        }
        public void find(int searchResult) {
            this.found = searchResult >= this.searchDC;
        }
        public String getDescription() {
            return description;
        }
        public HashMap<String, HashMap<Integer, String>> getSkills() {
            return skills;
        }
        public int getId() {
            return id;
        }

        public String getInfoAtOrBelowCheck(String skill, int checkResult) {
            StringBuilder result = new StringBuilder();
            skills.get(skill).forEach((check,info) -> {
                if (checkResult >= check) {
                    result.append(info).append("\n");
                }
            });
            return result.toString();
        }
    }

    private static class Corpse {
        private final Race race;
        private final Occupation job;
        private final Age age;
        private final TimeOfDeath tod;
        private final MurderMethod method;

        public Corpse() {
            this.race = Race.values()[rng.nextInt(Race.values().length)];
            this.job = Occupation.values()[rng.nextInt(Occupation.values().length)];
            this.age = Age.values()[rng.nextInt(Age.values().length)];
            this.tod = TimeOfDeath.values()[rng.nextInt(TimeOfDeath.values().length)];
            this.method = MurderMethod.values()[rng.nextInt(MurderMethod.values().length)];
        }
        public TimeOfDeath getTod() {
            return tod;
        }
        public MurderMethod getMethod() {
            return method;
        }
        public Race getRace() {
            return race;
        }
        public Occupation getJob() {
            return job;
        }
        public Age getAge() {
            return age;
        }
    }

    private enum TimeOfDeath {
        EARLY_NIGHT(0,2), LATE_NIGHT(3,5),
        EARLY_MORNING(6,8), LATE_MORNING(9,11),
        EARLY_AFTERNOON(12,14), LATE_AFTERNOON(15,17),
        EARLY_EVENING(18,20), LATE_EVENING(21,23);

        private final int start;
        private final int end;
        TimeOfDeath(int start, int end){
            this.start = start;
            this.end = end;
        }
        public boolean containsHour(int hour) {
            return this.start <= hour || hour <= this.end;
        }
    }

    private enum MurderMethod {
        STAB(false,false,"the victim has several deep stab wounds","muted punctures and sharp intakes of breath"),
        SLASH(false,false,"the victim has several lacerations and has lost a lot of blood","ripping and tearing"),
        CRUSH(false,false,"the victim has many contusions and broken bones","heavy blows"),
        POISON(false,false,"the victim died clutching their gut, wearing an agonized expression","coughing and retching"),
        BURN(true,true,"the victim has severe burns and charred skin","a blast and crackling"),
        SHOCK(true,true,"the victim has burst many small blood vessels and has minor burns","crackling and stuttering"),
        MELT(true,true,"the victim has severe burns, surrounded by bubbled and melted flesh","a sizzle and a scream"),
        FREEZE(true,true,"the victim has pale, rigid, cracked skin, and is particularly cold to the touch","a rush of wind"),
        EVOCATION(true,false, "a dim aura of evocation still surrounds the victim","a magical discharge"),
        CONJURATION(true,false,"a dim aura of conjuration still surrounds the victim","a magical portal opening and closing"),
        NECROMANCY(true,false,"a dim aura of necromancy still surrounds the victim","an otherworldly whisper");

        private final boolean isMagic;
        private final boolean isElemental;
        private final String description;
        private final String sound;
        MurderMethod(boolean isMagic, boolean isElemental, String description, String sound) {
            this.isMagic = isMagic;
            this.isElemental = isElemental;
            this.description = description;
            this.sound = sound;
        }
        public boolean isMagic() {
            return isMagic;
        }
        public boolean isElemental() {
            return isElemental;
        }
        public String getDescription() {
            return description;
        }
        public String getSound() {
            return sound;
        }
    }

    private static class MurderWeapon {
        private static HashMap<String, ArrayList<String>> weapons = new HashMap<>(){{
            this.put("STAB",new ArrayList<>(){{
                this.add("a dagger");
                this.add("a rapier");
                this.add("a pick");
                this.add("a spear");
                this.add("a short sword");
            }});
            this.put("SLASH",new ArrayList<>(){{
                this.add("a longsword");
                this.add("a sickle");
                this.add("a handaxe");
                this.add("a kukri");
            }});
            this.put("CRUSH",new ArrayList<>(){{
                this.add("a hammer");
                this.add("a mace");
                this.add("a club");
            }});
            this.put("POISON",new ArrayList<>(){{
                this.add("a vial of liquid");
                this.add("a sachet of powder");
            }});
            this.put("BURN",new ArrayList<>(){{
                this.add("a wand of Burning Hands");
                this.add("a wand of Scorching Ray");

            }});
            this.put("SHOCK",new ArrayList<>(){{
                this.add("a wand of Shocking Grasp");
            }});
            this.put("MELT",new ArrayList<>(){{
                this.add("a wand of Acid Splash");
                this.add("a wand of Acid Arrow");
            }});
            this.put("FREEZE",new ArrayList<>(){{
                this.add("a wand of Ray of Frost");
            }});
            this.put("EVOCATION",new ArrayList<>(){{
                this.add("a wand of Magic Missile");
            }});
            this.put("CONJURATION",new ArrayList<>(){{
                this.add("a wand of Summon Monster I");
            }});
            this.put("NECROMANCY",new ArrayList<>(){{
                this.add("a wand of Chill Touch");
                this.add("a wand of Ray of Enfeeblement");
            }});
        }};
        private final String description;
        MurderWeapon(MurderMethod method) {
            this.description = weapons.get(method.name()).get(rng.nextInt(weapons.size()));
        }
        public String getDescription() {
            return description;
        }
    }

    private static class NPCharacter {
        private final MurderWeapon holdingWeapon;
        private final Race race;
        private final Age age;
        private final SocialClass socClass;
        private final Occupation job;
        private final ArrayList<Quirk> quirks;

        public NPCharacter(MurderWeapon weapon) {
            this.holdingWeapon = weapon;
            this.race = Race.values()[rng.nextInt(Race.values().length)];
            this.age = Age.values()[rng.nextInt(Age.values().length)];
            this.socClass = SocialClass.values()[rng.nextInt(SocialClass.values().length)];
            this.job = Occupation.values()[rng.nextInt(Occupation.values().length)];
            this.quirks = new ArrayList<>(){{
                for (int i = 0; i < rng.nextInt(3); i++) {
                    this.add(Quirk.values()[rng.nextInt(Quirk.values().length)]);
                }
            }};
        }
        public NPCharacter() {
            this(null);
        }

        public MurderWeapon getHoldingWeapon() {
            return holdingWeapon;
        }
        public Race getRace() {
            return race;
        }
        public Age getAge() {
            return age;
        }
        public SocialClass getSocClass() {
            return socClass;
        }
        public Occupation getJob() {
            return job;
        }
        public ArrayList<Quirk> getQuirks() {
            return quirks;
        }
    }

    private enum Race {
        HUMAN("average-sized"),
        HALFLING("small"),
        ORC("large"),
        ELF("average length but narrow");

        private final String footprintSize;
        Race(String footprintSize) {
            this.footprintSize = footprintSize;
        }
        public String getFootprintSize() {
            return footprintSize;
        }
    }

    private enum Age {
        YOUNG("young adult"),
        MIDDLE_AGED("middle-aged"),
        OLD("elderly");

        private final String description;
        Age(String desc) {
            this.description = desc;
        }
        public String getDescription() {
            return description;
        }
    }

    private enum SocialClass {
        LOWER,MIDDLE,UPPER
    }

    private enum Occupation {
        //		Grocer(new Clue()),
        FISHMONGER(),
        JEWELER(),
        BLACKSMITH(),
        APOTHECARY(),
        BAKER(),
        BOWYER(),
        BREWER(),
        BUTCHER(),
        CARPENTER(),
        COBBLER(),
        COOK(),
        FLETCHER(),
        HABERDASHER(),
        MASON(),
        MILLER(),
        TOBACCONIST(),
        BARBER(),
        GARDENER(),
        PLASTERER(),
        PLUMBER(),
        TEAMSTER(),
        TINKER(),
        CHIMNEY_SWEEP(),
        GROOM(),
        GUARD(),
        SERVER(),
        BEGGAR();

        private final ArrayList <Clue> possibleClues;
        Occupation(Clue... inputClues){
            this.possibleClues = new ArrayList<>(){{
                for (Clue c : inputClues) {
                    this.add(c);
                }
            }};
        }
        public ArrayList<Clue> getPossibleClues() {
            return possibleClues;
        }
    }

    private enum Quirk {
        SMOKES,BAD_EYESIGHT,OTHER_QUIRK
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
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
 *  a clue should have:
 *      (quick-reference id for use with examine command?)
 *      physical description
 *      hash of possible skills +
 *          hash of check result thresholds + what you know if you beat that DC
 *
 *  a suspect should have:
 *      (also quick-ref for talk command?)
 *      description auto-genned by props
 *      hash of diplomacy attitude thresholds + what you know if you reach that level
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
Grocer,
Fishmonger,
Jeweler,
Blacksmith,
Apothecary,
Baker,
Bowyer,
Brewer,
Butcher,
Carpenter,
Cobbler,
Cook,
Fletcher,
Haberdasher,
Mason,
Miller,
Tobacconist,
Barber,
Gardener,
Plasterer,
Plumber,
Teamster,
Tinker,
Chimney Sweep,
Groom,
Guard,
Server,
Beggar,
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
s
Craft: Apothecary (Herbalist)                         Guild:        Apothecaries
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
Profession: Server (Bartender, serving wench)
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