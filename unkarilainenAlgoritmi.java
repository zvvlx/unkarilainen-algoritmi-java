package unkarilainenalgoritmi;


public class UnkarilainenAlgoritmi {
	private int[][] alkupArvot;
	private int[][] arvot; // kopio alkuperäisistä arvoista
	private int[][] viivat; //viivojen piirtelyä varten
	private int viivatLkm;
	
	int sarakkeet[]; //jokasen sarakkeen indeksi riveittäin, eli lopputulos
	int taytetytRivit[]; // optimointia varten että kaikille riville on määritelty sarake
	
        
	public UnkarilainenAlgoritmi(int[][] matriisi) {
		alkupArvot = matriisi;
		arvot = kopioiMatriisi(matriisi); 
		sarakkeet = new int[arvot.length];
		taytetytRivit = new int[arvot.length];
                printMatriisi(alkupArvot);
		
		//Algorithm
		sarakkeenPienimmallaVahennys(); //Vaihe 1
		rivinPienimmallaVahennys();	//Vaihe 2
		etsiNollat();			//Vaihe 3
		while(viivatLkm < arvot.length){
			nollienLisailya();	//Vaihe 4 (jos on tarpeen)
			etsiNollat();		//Vaihe 3 uudelleen
		}
		optimointi();			//Optimointi
                
                printMatriisi(arvot);           //Tulosteluja
                printLopputulos(sarakkeet);

	}
	

        //Vaihe 1: Vähennetään sarakkeista pienimmän arvon perusteella
	public void sarakkeenPienimmallaVahennys(){
		int sarakeMinValue[] = new int[arvot.length];
		//sarakkeittain pienin arvo ja talteen
		for(int sarake=0; sarake<arvot.length;sarake++){
			sarakeMinValue[sarake] = arvot[sarake][0];
			for(int rivi=1; rivi<arvot.length;rivi++){
				if(arvot[sarake][rivi] < sarakeMinValue[sarake])
					sarakeMinValue[sarake] = arvot[sarake][rivi];
			}
		}
		
                //itse vähentäminen
		for(int sarake=0; sarake<arvot.length;sarake++){
			for(int rivi=0; rivi<arvot.length;rivi++){
				arvot[sarake][rivi] -= sarakeMinValue[sarake];
			}
		}
	}
	

        //Vaihe 2: vähennetään riveiltä pienimmän arvon perusteella
	public void rivinPienimmallaVahennys(){
		int riviMinValue[] = new int[arvot.length];
		//riveittäin pienin arvo ja talteen
		for(int rivi=0; rivi<arvot.length;rivi++){
			riviMinValue[rivi] = arvot[0][rivi];
			for(int sarake=1; sarake<arvot.length;sarake++){
				if(arvot[sarake][rivi] < riviMinValue[rivi])
					riviMinValue[rivi] = arvot[sarake][rivi];
			}
		}
		
		//ja itse vähennys
		for(int rivi=0; rivi<arvot.length;rivi++){
			for(int sarake=0; sarake<arvot.length;sarake++){
				arvot[sarake][rivi] -= riviMinValue[rivi];
			}
		}
	} 
	

        //Vaihe 3.1: käydään sisältöä läpi ja jos arvona on nolla, merkataan viereiset solut
	public void etsiNollat(){
		viivatLkm = 0;
		viivat = new int[arvot.length][arvot.length];
		
		for(int sarake=0; sarake<arvot.length;sarake++){
			for(int rivi=0; rivi<arvot.length;rivi++){
				if(arvot[sarake][rivi] == 0)
					merkkaaViereiset(sarake, rivi, maksimiVH(sarake, rivi));
			}
		}
	}
	

        //Vaihe 3.2: katotaan kummassa suunnassa (vertikaali ja horisontaali) löytyy enemmän nollia. 
        //kun vertikaalisuunnassa löytyy 0, paluuarvoa kasvatetaan ja vastaavasti jos/kun horisontaalisuunnassa löytyy nollia, arvoa vähennetään
	private int maksimiVH(int sarake, int rivi){
		int result = 0;
		for(int i=0; i<arvot.length;i++){
			if(arvot[i][rivi] == 0)
				result++;
			if(arvot[sarake][i] == 0)
				result--;
		}
		return result;
	}
	

        //Vaihe 3.3: piirretään ns viiva merkkaamalla viereisiä soluja, parametrina annettava maksimiVH määrittelee kyseisen viivan suunnan
	private void merkkaaViereiset(int sarake, int rivi, int maksimiVH){
		if(viivat[sarake][rivi] == 2) //jos solu on ns risteyskohdassa, sitä ei merkata uudelleen
			return;                   
		
		if(maksimiVH > 0 && viivat[sarake][rivi] == 1) //jos solu on jo merkattu vertikaalisesti ja se olisi tarkotus tehdä uudelleen, niin ei tehdä mitään
			return; 
			
		if(maksimiVH <= 0 && viivat[sarake][rivi] == -1) //sama kuin ylempänä, mutta horisontaalisesti
			return; 
		
		for(int i=0; i<arvot.length; i++){ // Loopataan [sarake][rivi] indeksissä ja sen ympärillä
			if(maksimiVH > 0)	// if value of maksimiVH is positive, rivior vertically //jos maksimiVH arvo on positiivinen, merkataan vertikaalisti
				viivat[i][rivi] = viivat[i][rivi] == -1 || viivat[i][rivi] == 2 ? 2 : 1; //jos solu oli merkattu aikasemmin horisontaalisti (-1) ja nyt pitäs vertikaalisti (1), silloin se on risteyskohta. Ja jos sitä ei oltu aikasemmin, merkataan vertikaalisti
			else			//jos maksimiVH arvo on negatiivinen tai nolla, merkataan horisontaalisti  
				viivat[sarake][i] = viivat[sarake][i] == 1 || viivat[sarake][i] == 2 ? 2 : -1; //jos solu oli merkattu aikaisemmin vertikaalisti (1) ja nyt pitäis horisontaalisti (-1), silloinkin kyseessä on risteyskohta. Ja jos sitä ei oltu aikaisemmin, merkataan horisontaalisesti
		}
		
		viivatLkm++;
                //printMatriisi(viivat); 
	}
	

        //Vaihe 4 (tätä ei välttämättä toteuteta): haetaan pienimpiä arvoja soluista, joiden päältä "viiva" ei mene 
	public void nollienLisailya(){
		int pieninViivattomalleSolulle = 0; //määritellään nollaksi, koska tarkasteltavan solun arvo ei ole ainakaan 0 (koska sen yli menisi viiva)
		                                      
		// haetaan pienin arvo
		for(int sarake=0; sarake<arvot.length; sarake++){
			for(int rivi=0; rivi<arvot.length; rivi++){
				if(viivat[sarake][rivi] == 0 && (arvot[sarake][rivi] < pieninViivattomalleSolulle || pieninViivattomalleSolulle == 0))
					pieninViivattomalleSolulle = arvot[sarake][rivi];
			}
		}
		
                //vähennetään pienin arvo viivattomista soluista, sekä lisätään se solujen kohtiin jossa viivat menevät ristiin
		for(int sarake=0; sarake<arvot.length; sarake++){
			for(int rivi=0; rivi<arvot.length; rivi++){
				if(viivat[sarake][rivi] == 0) // ei viivaa -> vähennä
					arvot[sarake][rivi] -= pieninViivattomalleSolulle;
				
				else if(viivat[sarake][rivi] == 2) // risteyskohta -> lisää
					arvot[sarake][rivi] += pieninViivattomalleSolulle;
			}
		}
	} 
	

        //itse optimointi vaihe, eli määritetään jokaiselle sarakkeelle oma "solu" riveittäin, eli haetaan ns työtehtäville tekijät. ja koska sarakkeessa voi olla useampia nollia, 
        //pitää varmistaa että sarakkeiden solut on varmasti uniikeilta riveiltä, johon auttaa pieni brute force
	private boolean optimointi(int sarake){
		if(sarake == sarakkeet.length) // jos sarakkeet oli sijoitettu jo soluihin
			return true;
		
		for(int rivi=0; rivi<arvot.length; rivi++){ // kaikki rivit läpi
			if(arvot[sarake][rivi] == 0 && taytetytRivit[rivi] == 0){ //eli jos kyseinen solu kyseisellä rivillä on 0 ja se ei ole jo edellisen sarakkeen käytössä
				sarakkeet[sarake] = rivi; 
				taytetytRivit[rivi] = 1; // merkataan rivi täytetyksi
				if(optimointi(sarake+1)) //jos seuraavatkin sarakkeet menevät läpi, eli sarakkeet saivat solun uniikeilta riveiltä
					return true;           
				taytetytRivit[rivi] = 0; //jos ei ylläoleva toteutunu, kokeillaan eri solulla eri riviltä
			}                            
		}
		return false; //jos ei mitään solua sijoitettu kyseiseen sarakkeeseen, palataan sarakkeissa yks taaksepäin ja kokeillaan eri solulle eri riviltä
	}                  
	

	public boolean optimointi(){
		return optimointi(0);
	}
	

	public int[][] kopioiMatriisi(int[][] matriisi){
		int[][] tmp = new int[matriisi.length][matriisi.length];
		for(int sarake = 0; sarake < matriisi.length; sarake++){
			tmp[sarake] = matriisi[sarake].clone();
		}
		return tmp;
	}
	

	public void printMatriisi(int[][] matriisi){
		for(int sarake=0; sarake<matriisi.length; sarake++){
                    System.out.print("Työtehtävä " + sarake + " => ");
			for(int rivi=0; rivi<matriisi.length; rivi++){
				System.out.print(matriisi[sarake][rivi]+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
        
        public void printLopputulos(int[] lopputulos){
		for(int sarake=0; sarake<lopputulos.length; sarake++){
                    System.out.println("Työtehtävän " + sarake + " hoitaa henkilö indeksistä: " + lopputulos[sarake] );
                }
	}


public static void main(String[] args) {
    int[][] syotetytArvot = {   {250, 400, 350},
                                {400, 600, 350},
                                {200, 400, 250}
                            };
    
    int[][] syotetytArvot2 = {  {82, 83, 69, 92},
                                {77, 37, 49, 92},
                                {11, 69, 5, 86},
                                {8, 9, 98, 23}
                        };

    UnkarilainenAlgoritmi olio = new UnkarilainenAlgoritmi(syotetytArvot2);
    
    
    }
}