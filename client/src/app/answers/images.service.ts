import { Injectable } from "@angular/core";

const peopleImages = [
  // cspell:disable
  {
    name: "Russell M. Nelson",
    title: "First Presidency",
    source: "Nelson-Russell-M-small.jpg"
  },
  {
    name: "Dallin H. Oaks",
    title: "First Presidency",
    source: "dallin-h-oaks.jpg"
  },
  {
    name: "Henry B. Eyring",
    title: "First Presidency",
    source: "henry-b-eyring.jpg"
  },
  {
    name: "M. Russell Ballard",
    title: "Quorum of the Twelve Apostles",
    source: "m-russell-ballard.jpg"
  },
  {
    name: "Jeffrey R. Holland",
    title: "Quorum of the Twelve Apostles",
    source: "jeffrey-r-holland.jpg"
  },
  {
    name: "Dieter F. Uchtdorf",
    title: "Quorum of the Twelve Apostles",
    source: "dieter-f-uchtdorf.jpg"
  },
  {
    name: "David A. Bednar",
    title: "Quorum of the Twelve Apostles",
    source: "david-a-bednar.jpg"
  },
  {
    name: "Quentin L. Cook",
    title: "Quorum of the Twelve Apostles",
    source: "quentin-l-cook.jpg"
  },
  {
    name: "D. Todd Christofferson",
    title: "Quorum of the Twelve Apostles",
    source: "d-todd-christofferson.jpg"
  },
  {
    name: "Neil L. Andersen",
    title: "Quorum of the Twelve Apostles",
    source: "neil-l-andersen-small.jpg"
  },
  {
    name: "Ronald A. Rasband",
    title: "Quorum of the Twelve Apostles",
    source: "elder-rasband-2016-105.jpg"
  },
  {
    name: "Gary E. Stevenson",
    title: "Quorum of the Twelve Apostles",
    source: "stevenson-gary-e-smaller.jpg"
  },
  {
    name: "Dale G. Renlund",
    title: "Quorum of the Twelve Apostles",
    source: "elder-renlund-2016-105.jpg"
  },
  {
    name: "Gerrit W. Gong",
    title: "Quorum of the Twelve Apostles",
    source: "Gong-Gerrit-W-small.jpg"
  },
  {
    name: "Ulisses Soares",
    title: "Quorum of the Twelve Apostles",
    source: "Soares-Ulisses-small.jpg"
  },
  {
    name: "L. Whitney Clayton",
    title: "Presidency of the Seventy",
    source: "Clayton-L-Whitney-small.jpg"
  },
  {
    name: "Patrick Kearon",
    title: "Presidency of the Seventy",
    source: "Kearon-Patrick-C-small.jpg"
  },
  {
    name: "Carl B. Cook",
    title: "Presidency of the Seventy",
    source: "Cook-Carl-B-small.jpg"
  },
  {
    name: "Robert C. Gay",
    title: "Presidency of the Seventy",
    source: "Gay-Robert-C-small.jpg"
  },
  {
    name: "Terence M. Vinson",
    title: "Presidency of the Seventy",
    source: "Vinson-Terence-M-small.jpg"
  },
  {
    name: "José A. Teixeira",
    title: "Presidency of the Seventy",
    source: "Teixeira-Jose-A-small.jpg"
  },
  {
    name: "Carlos A. Godoy",
    title: "Presidency of the Seventy",
    source: "Godoy-Carlos-A-small.jpg"
  },
  {
    name: "Marcos A. Aidukaitis",
    title: "General Authority Seventies",
    source: "Aidukaitis-Marcos-A-small.jpg"
  },
  {
    name: "Rubén V. Alliaud",
    title: "General Authority Seventies",
    source: "aliaud-ruben-v-small.jpg"
  },
  {
    name: "Jose L. Alonso",
    title: "General Authority Seventies",
    source: "Alonso-Jose-L-small.jpg"
  },
  {
    name: "Jorge M. Alvarado",
    title: "General Authority Seventies",
    source: "alvarado-jorge-m-small.jpg"
  },
  {
    name: "Ian S. Ardern",
    title: "General Authority Seventies",
    source: "Ardern-Ian-S-small.jpg"
  },
  {
    name: "Steven R. Bangerter",
    title: "General Authority Seventies",
    source: "Bangerter-Steven-R-small.jpg"
  },
  {
    name: "W. Mark Bassett",
    title: "General Authority Seventies",
    source: "Bassett-W-Mark-small.jpg"
  },
  {
    name: "David S. Baxter",
    title: "General Authority Seventies",
    source: "Baxter-David-S-small.jpg"
  },
  {
    name: "Randall K. Bennett",
    title: "General Authority Seventies",
    source: "Bennett-Randall-K-small.jpg"
  },
  {
    name: "Hans T. Boom",
    title: "General Authority Seventies",
    source: "boom-hans-t-small.jpg"
  },
  {
    name: "Shayne M. Bowen",
    title: "General Authority Seventies",
    source: "Bowen-Shayne-M-small.jpg"
  },
  {
    name: "Mark A. Bragg",
    title: "General Authority Seventies",
    source: "Bragg-Mark-A-small.jpg"
  },
  {
    name: "L. Todd Budge",
    title: "General Authority Seventies",
    source: "budge-l-todd-small.jpg"
  },
  {
    name: "Matthew L. Carpenter",
    title: "General Authority Seventies",
    source: "Carpenter-Matthew-L-small.jpg"
  },
  {
    name: "Yoon Hwan Choi",
    title: "General Authority Seventies",
    source: "Choi-Yoon-Hwan-small.jpg"
  },
  {
    name: "Craig C. Christensen",
    title: "General Authority Seventies",
    source: "Christensen-Craig-C-small.jpg"
  },
  {
    name: "Weatherford T. Clayton",
    title: "General Authority Seventies",
    source: "Clayton-Weatherford-T-small.jpg"
  },
  {
    name: "Valeri V. Cordón",
    title: "General Authority Seventies",
    source: "Cordon-Valeri-V-small.jpg"
  },
  {
    name: "J. Devn Cornish",
    title: "General Authority Seventies",
    source: "Cornish-J-Devn-small.jpg"
  },
  {
    name: "Joaquin E. Costa",
    title: "General Authority Seventies",
    source: "Costa-Joaquin-E-small.jpg"
  },
  {
    name: "LeGrand R. Curtis Jr.",
    title: "General Authority Seventies",
    source: "Curtis-JR-LeGrand-R-small.jpg"
  },
  {
    name: "Massimo De Feo",
    title: "General Authority Seventies",
    source: "De-Feo-Massimo-small.jpg"
  },
  {
    name: "Benjamin De Hoyos",
    title: "General Authority Seventies",
    source: "De-Hoyos-Benjamin-small.jpg"
  },
  {
    name: "Edward Dube",
    title: "General Authority Seventies",
    source: "Dube-Edward-small.jpg"
  },
  {
    name: "Kevin R. Duncan",
    title: "General Authority Seventies",
    source: "Duncan-Kevin-R-small.jpg"
  },
  {
    name: "Timothy J. Dyches",
    title: "General Authority Seventies",
    source: "Dyches-Timothy-J-small.jpg"
  },
  {
    name: "David F. Evans",
    title: "General Authority Seventies",
    source: "Evans-David-F-small.jpg"
  },
  {
    name: "Enrique R. Falabella",
    title: "General Authority Seventies",
    source: "Falabella-Enrique-R-small.jpg"
  },
  {
    name: "Randy D. Funk",
    title: "General Authority Seventies",
    source: "Funk-Randy-D-small.jpg"
  },
  {
    name: "Eduardo Gavarret",
    title: "General Authority Seventies",
    source: "Gavarett-Eduardo-small.jpg"
  },
  {
    name: "Jack N. Gerard",
    title: "General Authority Seventies",
    source: "Gerard-Jack-N-small.jpg"
  },
  {
    name: "Ricardo P. Giménez",
    title: "General Authority Seventies",
    source: "gimenez-ricardo-p-small.jpg"
  },
  {
    name: "Taylor G. Godoy",
    title: "General Authority Seventies",
    source: "Godoy-Taylor-G-small.jpg"
  },
  {
    name: "Christoffel Golden",
    title: "General Authority Seventies",
    source: "Golden-Christoffel-small.jpg"
  },
  {
    name: "Walter F. González",
    title: "General Authority Seventies",
    source: "Gonzalez-Walter-F-small.jpg"
  },
  {
    name: "Brook P. Hales",
    title: "General Authority Seventies",
    source: "Hales-Brook-P-small.jpg"
  },
  {
    name: "Kevin S. Hamilton",
    title: "General Authority Seventies",
    source: "Hamilton-Kevin-S-small.jpg"
  },
  {
    name: "Allen D. Haynie",
    title: "General Authority Seventies",
    source: "Haynie-Allen-D-small.jpg"
  },
  {
    name: "Mathias Held",
    title: "General Authority Seventies",
    source: "Held-Mathias-small.jpg"
  },
  {
    name: "David P. Homer",
    title: "General Authority Seventies",
    source: "Homer-David-P-small.jpg"
  },
  {
    name: "Paul V. Johnson",
    title: "General Authority Seventies",
    source: "Johnson-Paul-V-small.jpg"
  },
  {
    name: "Peter M. Johnson",
    title: "General Authority Seventies",
    source: "johnson-peter-m-small.jpg"
  },
  {
    name: "Larry S. Kacher",
    title: "General Authority Seventies",
    source: "Kacher-Larry-S-small.jpg"
  },
  {
    name: "Jörg Klebingat",
    title: "General Authority Seventies",
    source: "Klebingat-Jorg-small.jpg"
  },
  {
    name: "Joni L. Koch",
    title: "General Authority Seventies",
    source: "Koch-Joni-L-small.jpg"
  },
  {
    name: "Erich W. Kopischke",
    title: "General Authority Seventies",
    source: "Kopischke-Erich-W-small.jpg"
  },
  {
    name: "Hugo E. Martinez",
    title: "General Authority Seventies",
    source: "Martinez-Hugo-E-small.jpg"
  },
  {
    name: "James B. Martino",
    title: "General Authority Seventies",
    source: "Martino-James-B-small.jpg"
  },
  {
    name: "Richard J. Maynes",
    title: "General Authority Seventies",
    source: "Maynes-Richard-J-small.jpg"
  },
  {
    name: "John A. McCune",
    title: "General Authority Seventies",
    source: "mccune-john-a-small.jpg"
  },
  {
    name: "Kyle S. McKay",
    title: "General Authority Seventies",
    source: "McKay-Kyle-S-small.jpg"
  },
  {
    name: "Peter F. Meurs",
    title: "General Authority Seventies",
    source: "Meurs-Peter-F-small.jpg"
  },
  {
    name: "Hugo Montoya",
    title: "General Authority Seventies",
    source: "Montoya-Hugo-small.jpg"
  },
  {
    name: "Marcus B. Nash",
    title: "General Authority Seventies",
    source: "Nash-Marcus-B-small.jpg"
  },
  {
    name: "K. Brett Nattress",
    title: "General Authority Seventies",
    source: "Nattress-Brett-K-small.jpg"
  },
  {
    name: "S. Gifford Nielsen",
    title: "General Authority Seventies",
    source: "Nielsen-S-Gifford-small.jpg"
  },
  {
    name: "Brent H. Nielson",
    title: "General Authority Seventies",
    source: "Nielson-Brent-H-small.jpg"
  },
  {
    name: "Adrián Ochoa",
    title: "General Authority Seventies",
    source: "Ochoa-Adrian-small.jpg"
  },
  {
    name: "S. Mark Palmer",
    title: "General Authority Seventies",
    source: "Palmer-S-Mark-small.jpg"
  },
  {
    name: "Adilson de Paula Parrella",
    title: "General Authority Seventies",
    source: "Parrella-Adilsonde-Paula-small.jpg"
  },
  {
    name: "Kevin W. Pearson",
    title: "General Authority Seventies",
    source: "Pearson-Kevin-W-small.jpg"
  },
  {
    name: "Anthony D. Perkins",
    title: "General Authority Seventies",
    source: "Perkins-Anthony-D-small.jpg"
  },
  {
    name: "Paul B. Pieper",
    title: "General Authority Seventies",
    source: "Pieper-Paul-B-small.jpg"
  },
  {
    name: "John C. Pingree Jr.",
    title: "General Authority Seventies",
    source: "Pingree-John-C-Jr-small.jpg"
  },
  {
    name: "Rafael E. Pino",
    title: "General Authority Seventies",
    source: "Pino-Rafael-E-small.jpg"
  },
  {
    name: "James R. Rasband",
    title: "General Authority Seventies",
    source: "rasband-james-r-small.jpg"
  },
  {
    name: "Michael T. Ringwood",
    title: "General Authority Seventies",
    source: "Ringwood-Michael-T-small.jpg"
  },
  {
    name: "Lynn G. Robbins",
    title: "General Authority Seventies",
    source: "Robbins-Lynn-G-small.jpg"
  },
  {
    name: "Gary B. Sabin",
    title: "General Authority Seventies",
    source: "Sabin-Gary-B-small.jpg"
  },
  {
    name: "Evan A. Schmutz",
    title: "General Authority Seventies",
    source: "Schmutz-Evan-A-small.jpg"
  },
  {
    name: "Joseph W. Sitati",
    title: "General Authority Seventies",
    source: "Sitati-Joseph-W-small.jpg"
  },
  {
    name: "Vern P. Stanfill",
    title: "General Authority Seventies",
    source: "Stanfill-Vern-P-small.jpg"
  },
  {
    name: "Benjamin M. Z. Tai",
    title: "General Authority Seventies",
    source: "tai-benjamin-m-z-small.jpg"
  },
  {
    name: "Brian K. Taylor",
    title: "General Authority Seventies",
    source: "Taylor-Brian-K-small.jpg"
  },
  {
    name: "Michael John U. Teh",
    title: "General Authority Seventies",
    source: "Teh-Michael-John-U-small.jpg"
  },
  {
    name: "Juan A. Uceda",
    title: "General Authority Seventies",
    source: "Uceda-Juan-A-small.jpg"
  },
  {
    name: "Arnulfo Valenzuela",
    title: "General Authority Seventies",
    source: "Valenzuela-Arnulfo-small.jpg"
  },
  {
    name: "Juan Pablo Villar",
    title: "General Authority Seventies",
    source: "Villar-Juan-Pablo-small.jpg"
  },
  {
    name: "Takashi Wada",
    title: "General Authority Seventies",
    source: "Wada-Takashi-small.jpg"
  },
  {
    name: "Taniela B. Wakolo",
    title: "General Authority Seventies",
    source: "Wakolo-Taniela-B-small.jpg"
  },
  {
    name: "Alan R. Walker",
    title: "General Authority Seventies",
    source: "walker-alan-r-small.jpg"
  },
  {
    name: "Scott D. Whiting",
    title: "General Authority Seventies",
    source: "Whiting-Scott-D-small.jpg"
  },
  {
    name: "Chi Hong (Sam) Wong",
    title: "General Authority Seventies",
    source: "Wong-Chi-Hong-Sam-small.jpg"
  },
  {
    name: "Kazuhiko Yamashita",
    title: "General Authority Seventies",
    source: "Yamashita-Kazuhiko-small.jpg"
  },
  {
    name: "Jorge F. Zeballos",
    title: "General Authority Seventies",
    source: "Zeballos-Jorge-F-small.jpg"
  },
  {
    name: "Gérald Caussé",
    title: "Presiding Bishopric",
    source: "gerald-causse.jpg"
  },
  {
    name: "Dean M. Davies",
    title: "Presiding Bishopric",
    source: "davies-dean-m-small-6-2016.jpg"
  },
  {
    name: "W. Christopher Waddell",
    title: "Presiding Bishopric",
    source: "w-christopher-waddell.jpg"
  },
  {
    name: "Jean B. Bingham",
    title: "Relief Society General Presidency",
    source: "jean-b-bingham-small-175x225-04-2017.jpg"
  },
  {
    name: "Sharon Eubank",
    title: "Relief Society General Presidency",
    source: "sharon-eubank-small-175x225-2017.jpg"
  },
  {
    name: "Reyna I. Aburto",
    title: "Relief Society General Presidency",
    source: "reyna-i-aburto-small-175x225-2017.jpg"
  },
  {
    name: "Bonnie H. Cordon",
    title: "Young Women General Presidency",
    source: "bonnie-cordon-small.jpg"
  },
  {
    name: "Michelle Craig",
    title: "Young Women General Presidency",
    source: "michelle-craig-small.jpg"
  },
  {
    name: "Becky Craven",
    title: "Young Women General Presidency",
    source: "becky-craven-small.jpg"
  },
  {
    name: "Joy D. Jones",
    title: "Primary General Presidency",
    source: "joy-d-jones-small.jpg"
  },
  {
    name: "Lisa L. Harkness",
    title: "Primary General Presidency",
    source: "lisa-harkness-small.jpg"
  },
  {
    name: "Cristina B. Franco",
    title: "Primary General Presidency",
    source: "cristina-b-franco-small.jpg"
  },
  {
    name: "Mark L. Pace",
    title: "Sunday School General Presidency",
    source: "pace-mark-l-small.jpg"
  },
  {
    name: "Milton Camargo",
    title: "Sunday School General Presidency",
    source: "camargo-milton-small.jpg"
  },
  {
    name: "Jan E. Newman",
    title: "Sunday School General Presidency",
    source: "newman-jan-e-small.jpg"
  },
  {
    name: "Stephen W. Owen",
    title: "Young Men General Presidency",
    source: "stephen_w_owen_small.jpg"
  },
  {
    name: "Douglas D. Holmes",
    title: "Young Men General Presidency",
    source: "douglas_d_holmes_small.jpg"
  },
  {
    name: "M. Joseph Brough",
    title: "Young Men General Presidency",
    source: "M_Joseph_Brough_small.jpg"
  }
  // cspell:enable
];

@Injectable()
export class ImagesService {
  private readonly nameMap = {};
  constructor() {
    peopleImages.forEach(
      personImageData =>
        (this.nameMap[
          personImageData.name
        ] = `/assets/people/${personImageData.source}`)
    );
  }

  getPerson(name: string): string | undefined {
    return this.nameMap[name];
  }
}
