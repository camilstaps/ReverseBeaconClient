package nl.camilstaps.rbn;

public enum Country {
	AD, AE, AF, AG, AL, AM, AO, AR, AS, AT, AU, AZ, BA, BB, BD, BE, BF, BG, BH, BI, BJ, BN, BO, BR,
	BS, BT, BW, BY, BZ, CA, CD, CF, CH, CI, CL, CM, CN, CO, CR, CU, CV, CY, CZ, DE, DJ, DK, DM, DO,
	DZ, EC, EE, EG, ER, ES, ET, FI, FJ, FM, FR, GA, GB, GD, GE, GH, GM, GN, GQ, GR, GT, GW, GY, HK,
	HN, HR, HT, HU, ID, IE, IL, IN, IQ, IR, IS, IT, JM, JO, JP, KE, KG, KH, KI, KM, KN, KP, KR, KW,
	KZ, LA, LB, LC, LI, LK, LR, LS, LT, LU, LV, LY, MA, MC, MD, ME, MG, MH, MK, ML, MM, MN, MO, MR,
	MT, MU, MV, MW, MX, MY, MZ, NA, NE, NG, NI, NL, NO, NP, NR, NZ, OM, PA, PE, PG, PH, PK, PL, PS,
	PT, PW, PY, QA, RO, RS, RU, RW, SA, SB, SC, SD, SE, SG, SI, SK, SL, SM, SN, SO, SR, ST, SV, SY,
	SZ, TD, TG, TH, TJ, TM, TN, TO, TP, TR, TT, TV, TW, TZ, UA, UG, US, UY, UZ, VA, VC, VE, VN, VU,
	WS, YE, ZA, ZM, ZW,
	UNITED_NATIONS,
	ICAO, // International Civil Aviation Organization @Todo flag
	WMO // World Meteorological Organization @Todo flag
	;

	/**
	 * A list of ITU prefixes and ISO 3166 country codes.
	 * p and c are Paired when p is the first prefix that is *not* part of c's callsign space.
	 */
	@SuppressWarnings("unchecked")
	static final Pair<String, Country>[] simplePrefixes = (Pair<String,Country>[]) new Pair[] {
			new Pair<>("3",  GB),
			new Pair<>("3B", MC),
			new Pair<>("3C", MU),
			new Pair<>("3D", GQ),
			new Pair<>("3DN", SZ),
			new Pair<>("3E", FJ),
			new Pair<>("3G", PA),
			new Pair<>("3H", CL),
			new Pair<>("3V", CN),
			new Pair<>("3W", TN),
			new Pair<>("3X", VN),
			new Pair<>("3Y", GN),
			new Pair<>("3Z", NO),

			new Pair<>("4",  PL),
			new Pair<>("4D", MX),
			new Pair<>("4J", PH),
			new Pair<>("4L", AZ),
			new Pair<>("4M", GE),
			new Pair<>("4O", VE),
			new Pair<>("4P", ME),
			new Pair<>("4T", LK),
			new Pair<>("4U", PE),
			new Pair<>("4V", UNITED_NATIONS),
			new Pair<>("4W", HT),
			new Pair<>("4X", TP), // @Todo East Timor flag
			new Pair<>("4Y", IL),
			new Pair<>("4Z", ICAO),

			new Pair<>("5",  IL),
			new Pair<>("5B", LY),
			new Pair<>("5C", CY),
			new Pair<>("5H", MA),
			new Pair<>("5J", TZ),
			new Pair<>("5L", CO),
			new Pair<>("5N", LR),
			new Pair<>("5P", NG),
			new Pair<>("5R", DK),
			new Pair<>("5T", MG),
			new Pair<>("5U", MR),
			new Pair<>("5V", NE),
			new Pair<>("5W", TG),
			new Pair<>("5X", WS),
			new Pair<>("5Y", UG),

			new Pair<>("6",  KE),
			new Pair<>("6C", EG),
			new Pair<>("6D", SY),
			new Pair<>("6K", MX),
			new Pair<>("6O", KR),
			new Pair<>("6P", SO),
			new Pair<>("6T", PK),
			new Pair<>("6V", SD),
			new Pair<>("6X", SN),
			new Pair<>("6Y", MG),
			new Pair<>("6Z", JM),

			new Pair<>("7",  LR),
			new Pair<>("7J", ID),
			new Pair<>("7O", JP),
			new Pair<>("7P", YE),
			new Pair<>("7Q", LS),
			new Pair<>("7R", MW),
			new Pair<>("7S", DZ),
			new Pair<>("7T", SE),
			new Pair<>("7Z", DZ),

			new Pair<>("8",  SA),
			new Pair<>("8J", ID),
			new Pair<>("8O", JP),
			new Pair<>("8P", BW),
			new Pair<>("8Q", BB),
			new Pair<>("8R", MV),
			new Pair<>("8S", GY),
			new Pair<>("8T", SE),
			new Pair<>("8Z", IN),

			new Pair<>("9",  SA),
			new Pair<>("9B", HR),
			new Pair<>("9E", IR),
			new Pair<>("9G", ET),
			new Pair<>("9H", GH),
			new Pair<>("9I", MT),
			new Pair<>("9K", ZM),
			new Pair<>("9L", KW),
			new Pair<>("9M", SL),
			new Pair<>("9N", MY),
			new Pair<>("9O", NP),
			new Pair<>("9U", CD),
			new Pair<>("9V", BI),
			new Pair<>("9W", SG),
			new Pair<>("9X", MY),
			new Pair<>("9Y", RW),

			new Pair<>("A",  TT),
			new Pair<>("A3", BW),
			new Pair<>("A4", TO),
			new Pair<>("A5", OM),
			new Pair<>("A6", BT),
			new Pair<>("A7", AE),
			new Pair<>("A8", QA),
			new Pair<>("A9", LR),
			new Pair<>("AA", BH),
			new Pair<>("AM", US),
			new Pair<>("AP", ES),
			new Pair<>("AT", PK),
			new Pair<>("AX", IN),
			new Pair<>("AY", AU),

			new Pair<>("B",  AR),
			new Pair<>("BM", CN),
			new Pair<>("BR", TW),
			new Pair<>("BU", CN),
			new Pair<>("BY", TW),

			new Pair<>("C",  CN),
			new Pair<>("C3", NR),
			new Pair<>("C4", AD),
			new Pair<>("C5", CY),
			new Pair<>("C6", GM),
			new Pair<>("C7", BS),
			new Pair<>("C8", WMO),
			new Pair<>("CA", MZ),
			new Pair<>("CF", CL),
			new Pair<>("CL", CA),
			new Pair<>("CN", CU),
			new Pair<>("CO", MA),
			new Pair<>("CP", CU),
			new Pair<>("CQ", BO),
			new Pair<>("CV", PT),
			new Pair<>("CY", UY),

			new Pair<>("D",  CA),
			new Pair<>("D4", AO),
			new Pair<>("D5", CV),
			new Pair<>("D6", LR),
			new Pair<>("D7", KM),
			new Pair<>("DA", KR),
			new Pair<>("DS", DE),
			new Pair<>("DU", KR),

			new Pair<>("E",  PH),
			new Pair<>("E3", TH),
			new Pair<>("E4", ER),
			new Pair<>("E5", PS),
			new Pair<>("E6", NZ), // @Todo: actually Cook Islands
			new Pair<>("E7", NZ), // @Todo: actually Niue
			new Pair<>("EA", BA),
			new Pair<>("EI", ES),
			new Pair<>("EK", IE),
			new Pair<>("EL", AM),
			new Pair<>("EM", LR),
			new Pair<>("EP", UA),
			new Pair<>("ER", IR),
			new Pair<>("ES", MD),
			new Pair<>("ET", EE),
			new Pair<>("EU", ET),
			new Pair<>("EX", BY),
			new Pair<>("EY", KG),
			new Pair<>("EZ", TJ),

			new Pair<>("F",  TM),

			new Pair<>("G",  FR),

			new Pair<>("H",  GB),
			new Pair<>("H3", CY),
			new Pair<>("H4", PA),
			new Pair<>("H6", SB),
			new Pair<>("H8", NI),
			new Pair<>("HA", PA),
			new Pair<>("HB", HU),
			new Pair<>("HB1", LI),
			new Pair<>("HB3Y", CH),
			new Pair<>("HB3Z", LI),
			new Pair<>("HBL", CH),
			new Pair<>("HBM", LI),
			new Pair<>("HC", CH),
			new Pair<>("HE", EC),
			new Pair<>("HF", CH),
			new Pair<>("HG", PL),
			new Pair<>("HH", HU),
			new Pair<>("HI", HT),
			new Pair<>("HJ", DO),
			new Pair<>("HL", CO),
			new Pair<>("HM", KR),
			new Pair<>("HN", KP),
			new Pair<>("HO", IQ),
			new Pair<>("HQ", PA),
			new Pair<>("HS", HN),
			new Pair<>("HT", TH),
			new Pair<>("HU", NI),
			new Pair<>("HV", SV),
			new Pair<>("HW", VA),
			new Pair<>("HZ", FR),

			new Pair<>("I",  SA),

			new Pair<>("J",  IT),
			new Pair<>("J3", DJ),
			new Pair<>("J4", GD),
			new Pair<>("J5", GR),
			new Pair<>("J6", GW),
			new Pair<>("J7", LC),
			new Pair<>("J8", DM),
			new Pair<>("JA", VC),
			new Pair<>("JT", JP),
			new Pair<>("JW", MN),
			new Pair<>("JY", NO),
			new Pair<>("JZ", JO),

			new Pair<>("K",  ID),

			new Pair<>("L",  US),
			new Pair<>("LA", AR),
			new Pair<>("LO", NO),
			new Pair<>("LX", AR),
			new Pair<>("LY", LU),
			new Pair<>("LZ", LT),

			new Pair<>("M",  BG),

			new Pair<>("N",  GB),

			new Pair<>("O",  US),
			new Pair<>("OD", PE),
			new Pair<>("OE", LB),
			new Pair<>("OF", AT),
			new Pair<>("OK", FI),
			new Pair<>("OM", CZ),
			new Pair<>("ON", SK),
			new Pair<>("OU", BE),

			new Pair<>("P",  DK),
			new Pair<>("P3", PG),
			new Pair<>("P4", CY),
			new Pair<>("P5", NL), // @Todo actually Aruba
			new Pair<>("PA", KP),
			new Pair<>("PK", NL),
			new Pair<>("PP", ID),
			new Pair<>("PZ", BR),

			new Pair<>("Q",  SR),

			new Pair<>("S",  RU),
			new Pair<>("S5", BD),
			new Pair<>("S6", SI),
			new Pair<>("S7", SG),
			new Pair<>("S8", SC),
			new Pair<>("S9", ZA),
			new Pair<>("SA", ST),
			new Pair<>("SN", SE),
			new Pair<>("SS", PL),
			new Pair<>("SSN", EG),
			new Pair<>("SU", SD),
			new Pair<>("SV", EG),

			new Pair<>("T",  GR),
			new Pair<>("T3", TV),
			new Pair<>("T4", KI),
			new Pair<>("T5", CU),
			new Pair<>("T6", SO),
			new Pair<>("T7", AF),
			new Pair<>("T8", SM),
			new Pair<>("T9", PW),
			new Pair<>("TD", TR),
			new Pair<>("TE", GT),
			new Pair<>("TF", CR),
			new Pair<>("TG", IS),
			new Pair<>("TH", GT),
			new Pair<>("TI", FR),
			new Pair<>("TJ", CR),
			new Pair<>("TK", CM),
			new Pair<>("TL", FR),
			new Pair<>("TM", CF),
			new Pair<>("TN", FR),
			new Pair<>("TO", CD),
			new Pair<>("TR", FR),
			new Pair<>("TS", GA),
			new Pair<>("TT", TN),
			new Pair<>("TU", TD),
			new Pair<>("TV", CI),
			new Pair<>("TY", FR),
			new Pair<>("TZ", BJ),

			new Pair<>("U",  ML),
			new Pair<>("UJ", RU),
			new Pair<>("UN", UZ),
			new Pair<>("UR", KZ),

			new Pair<>("V",  UA),
			new Pair<>("V3", AG),
			new Pair<>("V4", BZ),
			new Pair<>("V5", KN),
			new Pair<>("V6", NA),
			new Pair<>("V7", FM),
			new Pair<>("V8", MH),
			new Pair<>("VA", BN),
			new Pair<>("VH", CA),
			new Pair<>("VO", AU),
			new Pair<>("VP", CA),
			new Pair<>("VR", GB),
			new Pair<>("VS", HK),
			new Pair<>("VT", GB),
			new Pair<>("VX", IN),
			new Pair<>("VZ", CA),

			new Pair<>("W",  AU),

			new Pair<>("X",  US),
			new Pair<>("XJ", MX),
			new Pair<>("XP", CA),
			new Pair<>("XQ", DK),
			new Pair<>("XS", CL),
			new Pair<>("XT", CN),
			new Pair<>("XU", BF),
			new Pair<>("XV", KH),
			new Pair<>("XW", VN),
			new Pair<>("XX", LA),
			new Pair<>("XY", MO),

			new Pair<>("Y",  MM),
			new Pair<>("YA", GB),
			new Pair<>("YB", AF),
			new Pair<>("YI", ID),
			new Pair<>("YJ", IQ),
			new Pair<>("YK", VU),
			new Pair<>("YL", SY),
			new Pair<>("YM", LV),
			new Pair<>("YN", TR),
			new Pair<>("YO", NI),
			new Pair<>("YS", RO),
			new Pair<>("YT", SV),
			new Pair<>("YV", RS),

			new Pair<>("Z",  VE),
			new Pair<>("Z3", ZW),
			new Pair<>("Z8", MK),
			new Pair<>("ZA", SD), // @Todo actually South Sudan
			new Pair<>("ZB", AL),
			new Pair<>("ZK", GB),
			new Pair<>("ZN", NZ),
			new Pair<>("ZP", GB),
			new Pair<>("ZQ", PY),
			new Pair<>("ZR", GB),
			new Pair<>("ZV", ZA),

			new Pair<>("[",  BR),
	};

	public static Country fromCallsign(Callsign callsign) {
		char[] prefix = callsign.toString().substring(0, 3).toCharArray();

		for (Pair<String, Country> check : simplePrefixes)
			if (before(prefix, check.first))
				return check.second;

		throw new IllegalArgumentException();
	}

	private static boolean before(char[] prefix, String check) {
		int len = Math.min(prefix.length, check.length());
		for (int i = 0; i < 3; i++) {
			if (len <= i)
				return false;
			if (prefix[i] < check.charAt(i))
				return true;
			if (prefix[i] > check.charAt(i))
				return false;
		}
		return false;
	}

	private static class Pair<A,B> {
		final A first;
		final B second;

		Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}
}