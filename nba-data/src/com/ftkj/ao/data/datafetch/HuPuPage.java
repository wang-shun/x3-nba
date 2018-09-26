package com.ftkj.ao.data.datafetch;


import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class HuPuPage {
	
	static ESPNPageAnalyzer espn =new ESPNPageAnalyzer();
	
	/*
	public void getInfo(int playerId,String url){
		try{
			StringBuffer sql = new StringBuffer("update player_info set ");
			String[] strs = getPlayerInfo(url).split("[|]");
			for(String one:strs){
				if(one.indexOf("位置:")!=-1 && one.indexOf("号")!=-1){
					sql.append("number='").append(one.split("[(]")[1].replace("号)", "")).append("',");
				}
				if(one.indexOf("身高:")!=-1){
					sql.append("height='").append(one.replace("身高:", "")).append("',");
				}
				if(one.indexOf("体重:")!=-1){
					sql.append("weight='").append(one.replace("体重:", "")).append("',");
				}
				if(one.indexOf("生日:")!=-1){
					sql.append("birthday='").append(one.replace("生日:", "")).append("',");
				}
				if(one.indexOf("选秀:")!=-1){
					sql.append("draft='").append(one.replace("选秀:", "")).append("',");
				}
				if(one.indexOf("学校:")!=-1){
					sql.append("school='").append(one.replace("学校:", "")).append("',");
				}
				if(one.indexOf("国籍:")!=-1){
					sql.append("nation='").append(one.replace("国籍:", "")).append("' ");
				}				
			}
			sql.append(" ,team_ids=0 where player_id = "+playerId).append(";");
			System.out.println(sql.toString());
		}catch(Exception e){}
	}
	*/
	public void getInfo(int playerId,String url){
		try{
			StringBuffer sql = new StringBuffer("update player_info set ");
			String[] strs = getPlayerInfo(url).split("[|]");
			for(String one:strs){
				//System.out.println("///"+one);
				if(one.indexOf("位置:")!=-1 ){
					sql.append("hupu_pos='").append(one.split("[(]")[0].replace("位置:","")).append("' ");
				}
				/*
				if(one.indexOf("身高:")!=-1){
					sql.append("height='").append(one.replace("身高:", "")).append("',");
				}
				if(one.indexOf("体重:")!=-1){
					sql.append("weight='").append(one.replace("体重:", "")).append("',");
				}
				if(one.indexOf("生日:")!=-1){
					sql.append("birthday='").append(one.replace("生日:", "")).append("',");
				}
				if(one.indexOf("选秀:")!=-1){
					sql.append("draft='").append(one.replace("选秀:", "")).append("',");
				}
				if(one.indexOf("学校:")!=-1){
					sql.append("school='").append(one.replace("学校:", "")).append("',");
				}
				if(one.indexOf("国籍:")!=-1){
					sql.append("nation='").append(one.replace("国籍:", "")).append("' ");
				}
				*/		
			}
			sql.append(" where player_id = "+playerId).append(";");
			System.out.println(sql.toString());
		}catch(Exception e){}
	}
	
	private String getPlayerInfo(String url) throws PageAnalyzerException{
		String content=espn.getURLPageContent("http://nba.hupu.com/players/"+url,"gb2312");
		HtmlCleaner htmlCleaner=new HtmlCleaner();
		StringBuffer str = new StringBuffer();
		try {
			TagNode root=htmlCleaner.clean(content);
			TagNode nodes[]=root.getElementsByAttValue("class","players_content_padding",true,false);			
			TagNode mNode=nodes[0];			
			int nodeSize = 0;
			String str1 = "",str2 = "";
			for(TagNode node:mNode.getAllElements(true)){
				if(node.getName().equals("tr")){
					nodeSize = node.getChildTags().length;
					if(nodeSize==2){
						str1 = node.getChildTags()[0].getText().toString().replace(" ", "");
						str2 = node.getChildTags()[1].getText().toString().replace(" ", "");
						if(str1.indexOf("身高:")!=-1){
							str1 = str1.replace("\n", "|");
							//System.out.println("==="+str1);
							str.append(str1).append("|");
						}if(str2.indexOf("选秀:")!=-1){
							str2 = str2.replace("选秀:\n", "选秀:").replace("\n", "|");
							//System.out.println("==="+str2);
							str.append(str2).append("|");
						}
					}
				}			
			}
		} catch (Exception e)	 {
			e.printStackTrace();
		}
		//System.out.println("//"+str);
		return str.toString();
	}
	
	public static void main(String[] args){		
		HuPuPage page = new HuPuPage();
		/*
		page.getInfo(3988,"dannygreen_3356.html");		
		page.getInfo(4008,"jeffpendergraph_3318.html");
		page.getInfo(4025,"diontechristmas_3710.html");
		page.getInfo(4023,"garretttemple_3440.html");
		page.getInfo(2991,"ronniebrewer_1216.html");
		page.getInfo(2011,"kylekorver_645.html");
		page.getInfo(3986,"tajgibson_3357.html");
		page.getInfo(3438,"georgehill_3180.html");
		page.getInfo(1996,"mattbonner_839.html");
		page.getInfo(215,"timduncan_693.html");
		page.getInfo(3965,"dejuanblair_3316.html");
		page.getInfo(4300,"garyneal_3512.html");
		page.getInfo(3233,"tiagosplitter_1217.html");
		page.getInfo(2367,"tonyallen_829.html");
		page.getInfo(4241,"xavierhenry_3460.html");
		page.getInfo(4291,"greivisvasquez_3476.html");
		page.getInfo(1982,"chriskaman_225.html");
		page.getInfo(3003,"randyfoye_1215.html");
		page.getInfo(1767,"rasualbutler_1078.html");
		page.getInfo(4238,"ericbledsoe_3466.html");
		page.getInfo(996,"paugasol_15.html");
		page.getInfo(2755,"franciscogarcia_1178.html");
		page.getInfo(3462,"jasonthompson_3045.html");
		page.getInfo(3983,"tyrekeevans_3349.html");
		page.getInfo(4258,"demarcuscousins_3453.html");
		page.getInfo(1987,"dwyanewade_50.html");
		page.getInfo(2009,"jamesjones_390.html");
		page.getInfo(2184,"udonishaslem_54.html");
		page.getInfo(558,"mikemiller_16.html");
		page.getInfo(1977,"chrisbosh_419.html");
		page.getInfo(3024,"jjredick_1213.html");
		page.getInfo(3971,"earlclark_3329.html");
		page.getInfo(4263,"danielorton_3477.html");
		page.getInfo(3038,"shawnewilliams_1245.html");
		page.getInfo(3455,"anthonyrandolph_3026.html");
		page.getInfo(3194,"wilsonchandler_1424.html");
		page.getInfo(1727,"amarestoudemire_180.html");
		page.getInfo(4274,"landryfields_3487.html");
		page.getInfo(1726,"johnsalmons_775.html");
		page.getInfo(3451,"lucrichardmbahamoute_3072.html");
		page.getInfo(1999,"carlosdelfino_718.html");
		page.getInfo(2767,"ersanilyasova_1141.html");
		page.getInfo(4265,"larrysanders_3463.html");
		page.getInfo(1781,"luisscola_1443.html");
		page.getInfo(976,"shanebattier_20.html");
		page.getInfo(2834,"chuckhayes_1200.html");
		page.getInfo(3445,"courtneylee_3052.html");
		page.getInfo(3968,"chasebudinger_3312.html");
		page.getInfo(3994,"jordanhill_3305.html");
		page.getInfo(4264,"patrickpatterson_3462.html");
		page.getInfo(2751,"montaellis_1111.html");
		page.getInfo(3242,"brandanwright_1238.html");
		page.getInfo(2772,"davidlee_1032.html");
		page.getInfo(4261,"ekpeudoh_3454.html");
		page.getInfo(1007,"joejohnson_188.html");
		page.getInfo(2411,"joshsmith_502.html");
		page.getInfo(3213,"alhorford_1239.html");
		page.getInfo(2797,"marvinwilliams_1054.html");
		page.getInfo(165,"jamalcrawford_572.html");
		page.getInfo(4243,"jordancrawford_3475.html");
		page.getInfo(261,"kevingarnett_721.html");
		page.getInfo(3200,"glendavis_1366.html");
		page.getInfo(662,"paulpierce_35.html");
		page.getInfo(615,"jermaineoneal_372.html");
		page.getInfo(4240,"averybradley_3467.html");
		page.getInfo(3930,"cartiermartin_3222.html");
		page.getInfo(1981,"kirkhinrich_324.html");
		page.getInfo(469,"rashardlewis_193.html");
		page.getInfo(3243,"nickyoung_1352.html");
		page.getInfo(2746,"andrayblatche_1153.html");
		page.getInfo(4289,"kevinseraphin_3465.html");
		page.getInfo(1026,"geraldwallace_749.html");
		page.getInfo(2167,"borisdiaw_312.html");
		page.getInfo(3993,"geraldhenderson_3308.html");
		page.getInfo(3235,"rodneystuckey_1358.html");
		page.getInfo(2775,"jasonmaxiell_1044.html");
		page.getInfo(3998,"jonasjerebko_3360.html");
		page.getInfo(4260,"gregmonroe_3455.html");
		page.getInfo(3010,"solomonjones_1291.html");
		page.getInfo(1708,"mikedunleavy_207.html");
		page.getInfo(3457,"brandonrush_3031.html");
		page.getInfo(2760,"dannygranger_1042.html");
		page.getInfo(3991,"tylerhansbrough_1354.html");
		page.getInfo(4251,"paulgeorge_3458.html");
		page.getInfo(2419,"andersonvarejao_659.html");
		page.getInfo(3231,"ramonsessions_1431.html");
		page.getInfo(385,"antawnjamison_471.html");
		page.getInfo(2386,"andreiguodala_642.html");
		page.getInfo(3460,"marreesespeights_3050.html");
		page.getInfo(3244,"thaddeusyoung_1237.html");
		page.getInfo(2799,"louiswilliams_1128.html");
		page.getInfo(91,"eltonbrand_222.html");
		page.getInfo(4239,"evanturner_3450.html");
		page.getInfo(841,"jasonterry_567.html");
		page.getInfo(2774,"ianmahinmi_1446.html");
		page.getInfo(510,"shawnmarion_182.html");
		page.getInfo(2768,"jarrettjack_1055.html");
		page.getInfo(3232,"jasonsmith_1342.html");
		page.getInfo(4017,"marcusthornton_3354.html");
		page.getInfo(136,"vincecarter_936.html");
		page.getInfo(3423,"gorandragic_3074.html");
		page.getInfo(3201,"jareddudley_1395.html");
		page.getInfo(2754,"channingfrye_1029.html");
		page.getInfo(3418,"michaelbeasley_3003.html");
		page.getInfo(3449,"kevinlove_3004.html");
		page.getInfo(2795,"martellwebster_1034.html");
		page.getInfo(4247,"wesleyjohnson_3452.html");
		page.getInfo(2983,"lamarcusaldridge_1209.html");
		page.getInfo(3416,"nicolasbatum_3025.html");
		page.getInfo(3974,"dantecunningham_3367.html");
		page.getInfo(3028,"thabosefolosha_1272.html");
		page.getInfo(1978,"nickcollison_199.html");
		page.getInfo(3209,"jeffgreen_1349.html");
		page.getInfo(3202,"kevindurant_1236.html");
		page.getInfo(3439,"sergeibaka_3055.html");
		page.getInfo(4005,"byronmullens_3389.html");
		page.getInfo(2389,"aljefferson_827.html");
		page.getInfo(2778,"cjmiles_1051.html");
		page.getInfo(4249,"gordonhayward_3457.html");
		page.getInfo(4257,"derrickfavors_3451.html");
		page.getInfo(2769,"amirjohnson_1137.html");
		page.getInfo(3417,"jerrydbayless_3011.html");
		page.getInfo(2987,"andreabargnani_1210.html");
		page.getInfo(3978,"demarderozan_3314.html");
		page.getInfo(4259,"eddavis_3461.html");
		page.getInfo(63,"chaunceybillups_709.html");
		page.getInfo(1135,"chrisandersen_526.html");
		page.getInfo(1713,"nene_253.html");
		page.getInfo(6430,"jimmybutler_3583.html");
		page.getInfo(6436,"jordanhamilton_3579.html");
		page.getInfo(6428,"marshonbrooks_3578.html");
		page.getInfo(6464,"donatasmotiejunas_3573.html");
		page.getInfo(6470,"chrissingleton_3571.html");
		page.getInfo(6468,"imanshumpert_3570.html");
		page.getInfo(6478,"nikolavucevic_3569.html");
		page.getInfo(6429,"alecburks_3565.html");
		page.getInfo(6475,"klaythompson_3564.html");
		page.getInfo(4165,"janvesely_3559.html");
		page.getInfo(6480,"derrickwilliams_3555.html");
		page.getInfo(6450,"kawhileonard_3568.html");
		page.getInfo(6427,"bismackbiyombo_3560.html");
		page.getInfo(6474,"tristanthompson_3557.html");
		page.getInfo(6460,"etwaunmoore_3608.html");
		page.getInfo(6439,"joshharrellson_3599.html");
		page.getInfo(6485,"lancethomas_3619.html");
		page.getInfo(6546,"gustavoayon_3618.html");
		page.getInfo(6543,"julyanstone_3622.html");
		page.getInfo(6530,"gregsmith_3631.html");
		page.getInfo(6569,"alananderson_1109.html");
		page.getInfo(2761,"geraldgreen_1028.html");
		page.getInfo(6631,"tylerzeller_3654.html");
		page.getInfo(6597,"terrencejones_3655.html");
		page.getInfo(6630,"tonywroten_3662.html");
		*/
		page.getInfo(6616,"milesplumlee_3663.html");
		page.getInfo(6598,"perryjones_3665.html");
		page.getInfo(6156,"jefftaylor_3668.html");
		page.getInfo(6593,"bernardjames_3670.html");
		page.getInfo(6581,"jaecrowder_3671.html");
		page.getInfo(6595,"orlandojohnson_3673.html");
		page.getInfo(6576,"quincyacy_3674.html");
		page.getInfo(6609,"khrismiddleton_3676.html");
		page.getInfo(6579,"willbarton_3677.html");
		page.getInfo(6625,"tyshawntaylor_3678.html");
		page.getInfo(6615,"kyleoquinn_3686.html");
		page.getInfo(6633,"robbiehummel_3695.html");
		page.getInfo(6583,"anthonydavis_3638.html");
		page.getInfo(6617,"austinrivers_3647.html");
		page.getInfo(6585,"andredrummond_3646.html");
		page.getInfo(3972,"victorclaver_3390.html");
		page.getInfo(3004,"joelfreeland_1285.html");
		page.getInfo(2991473,"anthonybennett_3763.html");
		page.getInfo(2527963,"victoroladipo_3764.html");
		page.getInfo(2579258,"codyzeller_3766.html");
		page.getInfo(2991280,"nerlensnoel_3768.html");
		page.getInfo(2578213,"benmclemore_3769.html");
		page.getInfo(2490149,"c.j.mcollum_3773.html");
		page.getInfo(2993875,"shabazzmuhammad_3777.html");
		page.getInfo(3032977,"giannisantetokounmpo_3778.html");
		page.getInfo(2959745,"sergeykarasev_3782.html");
		page.getInfo(2528353,"tonysnell_3783.html");
		page.getInfo(2488653,"masonplumlee_3785.html");
		page.getInfo(2528779,"reggiebullock_3788.html");
		page.getInfo(2530596,"andreroberson_3789.html");
		page.getInfo(2959736,"nemanjanedovic_3793.html");
		page.getInfo(2531210,"allencrabbe_3794.html");
		page.getInfo(2488660,"glenricejr_3798.html");
		
	}
}
