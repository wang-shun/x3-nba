package com.ftkj.invoker;

public enum ServerType {

	Server_nba_data("Server_nba_data"),	
	Server_nba_account("Server_nba_account"),	
	Server_nba_game("Server_nba_game"),	
	
	Server_nba_game_group("Server_nba_game_group"),

	Server_game_100("100"),	

	Server_game_301("301"),	
	Server_game_302("302"),	
	Server_game_303("303"),	
	Server_game_304("304"),	
	Server_game_305("305"),	
	Server_game_306("306"),	
	Server_game_307("307"),
	Server_game_308("308"),
	Server_game_309("309"),
	Server_game_310("310"),

	Server_game_401("401"),	
	Server_game_402("402"),	
	Server_game_403("403"),	
	Server_game_404("404"),	
	Server_game_405("405"),	
	Server_game_406("406"),	
	Server_game_407("407"),
	Server_game_408("408"),
	Server_game_409("409"),
	Server_game_410("410"),

	Server_game_501("501"),	
	Server_game_502("502"),	
	Server_game_503("503"),	
	Server_game_504("504"),	
	Server_game_505("505"),	
	Server_game_506("506"),	
	Server_game_507("507"),
	Server_game_508("508"),
	Server_game_509("509"),
	Server_game_510("510"),	

	Server_game_101("101"),	
	Server_game_102("102"),	
	Server_game_103("103"),	
	Server_game_104("104"),	
	Server_game_105("105"),	
	Server_game_106("106"),	
	Server_game_107("107"),	
	Server_game_108("108"),	
	Server_game_109("109"),	
	Server_game_110("110"),	

	Server_game_111("111"),	
	Server_game_112("112"),	
	Server_game_113("113"),	
	Server_game_114("114"),	
	Server_game_115("115"),	
	Server_game_116("116"),	
	Server_game_117("117"),	
	Server_game_118("118"),	
	Server_game_119("119"),	
	Server_game_120("120"),

	Server_game_121("121"),
	Server_game_122("122"),
	Server_game_123("123"),
	Server_game_124("124"),
	Server_game_125("125"),
	Server_game_126("126"),
	Server_game_127("127"),
	Server_game_128("128"),
	Server_game_129("129"),
	Server_game_130("130"),

	Server_game_131("131"),
	Server_game_132("132"),
	Server_game_133("133"),
	Server_game_134("134"),
	Server_game_135("135"),
	Server_game_136("136"),
	Server_game_137("137"),
	Server_game_138("138"),
	Server_game_139("139"),
	Server_game_140("140"),

	Server_game_141("141"),
	Server_game_142("142"),
	Server_game_143("143"),
	Server_game_144("144"),
	Server_game_145("145"),
	Server_game_146("146"),
	Server_game_147("147"),
	Server_game_148("148"),
	Server_game_149("149"),
	Server_game_150("150"),

	Server_game_151("151"),
	Server_game_152("152"),
	Server_game_153("153"),
	Server_game_154("154"),
	Server_game_155("155"),
	Server_game_156("156"),
	Server_game_157("157"),
	Server_game_158("158"),
	Server_game_159("159"),
	Server_game_160("160"),

	Server_game_161("161"),
	Server_game_162("162"),
	Server_game_163("163"),
	Server_game_164("164"),
	Server_game_165("165"),
	Server_game_166("166"),
	Server_game_167("167"),
	Server_game_168("168"),
	Server_game_169("169"),
	Server_game_170("170"),

	Server_game_171("171"),
	Server_game_172("172"),
	Server_game_173("173"),
	Server_game_174("174"),
	Server_game_175("175"),
	Server_game_176("176"),
	Server_game_177("177"),
	Server_game_178("178"),
	Server_game_179("179"),
	Server_game_180("180"),

	Server_game_181("181"),
	Server_game_182("182"),
	Server_game_183("183"),
	Server_game_184("184"),
	Server_game_185("185"),
	Server_game_186("186"),
	Server_game_187("187"),
	Server_game_188("188"),
	Server_game_189("189"),
	Server_game_190("190"),

	Server_game_191("191"),
	Server_game_192("192"),
	Server_game_193("193"),
	Server_game_194("194"),
	Server_game_195("195"),
	Server_game_196("196"),
	Server_game_197("197"),
	Server_game_198("198"),
	Server_game_199("199"),
	Server_game_200("200"),
	
	Server_game_201("201"),
	Server_game_202("202"),
	Server_game_203("203"),
	Server_game_204("204"),
	Server_game_205("205"),
	Server_game_206("206"),
	Server_game_207("207"),
	Server_game_208("208"),
	Server_game_209("209"),
	Server_game_210("210"),
	
	Server_game_211("211"),
	Server_game_212("212"),
	Server_game_213("213"),
	Server_game_214("214"),
	Server_game_215("215"),
	Server_game_216("216"),
	Server_game_217("217"),
	Server_game_218("218"),
	Server_game_219("219"),
	Server_game_220("220"),
	
	Server_game_221("221"),
	Server_game_222("222"),
	Server_game_223("223"),
	Server_game_224("224"),
	Server_game_225("225"),
	Server_game_226("226"),
	Server_game_227("227"),
	Server_game_228("228"),
	Server_game_229("229"),
	Server_game_230("230"),
	
	Server_game_231("231"),
	Server_game_232("232"),
	Server_game_233("233"),
	Server_game_234("234"),
	Server_game_235("235"),
	Server_game_236("236"),
	Server_game_237("237"),
	Server_game_238("238"),
	Server_game_239("239"),
	Server_game_240("240"),
	
	Server_game_241("241"),
	Server_game_242("242"),
	Server_game_243("243"),
	Server_game_244("244"),
	Server_game_245("245"),
	;

	private final String resName;   

	ServerType(String resName) { 
		this.resName = resName;         
	}   

	public String getResName() { 
		return resName; 
	}

	public static ServerType getServerByShardId(int shardId){
		ServerType[] list = ServerType.values();
		for(ServerType t:list){
			if(t.getResName().equals(""+shardId))return t;
		}
		return null;
	}
}
