package com.benkio.richardphjbensonbot.data

import cats._
import com.benkio.telegrambotinfrastructure.model._

object Video {

  def messageRepliesVideoData[F[_]: Applicative]: List[ReplyBundleMessage[F]] = List(
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("amici veri"),
        StringTextTriggerValue("soldati")
      ),
      List(
        MediaFile("rphjb_AmiciVeriVecchiSoldati.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("gianni neri")
      ),
      List(
        MediaFile("rphjb_RingraziareGianniTraffico.mp4"),
        MediaFile("rphjb_GianniNeriCoppiaMiciciale.mp4"),
        MediaFile("rphjb_GianniNeriCheFineHaiFatto.mp4")
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("accor(data|dana)".r)
      ),
      List(
        MediaFile("rphjb_Accordana.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("\\brap\\b".r),
        StringTextTriggerValue("musica italiana"),
        StringTextTriggerValue("tullio pane"),
        StringTextTriggerValue("otello profazio"),
        StringTextTriggerValue("mario lanza"),
        StringTextTriggerValue("gianni celeste"),
        StringTextTriggerValue("luciano tajoli")
      ),
      List(
        MediaFile("rphjb_RapMusicaMelodicaListaCantanti.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("eric clapton"),
        RegexTextTriggerValue("uo(m)+ini d'affari".r),
      ),
      List(
        MediaFile("rphjb_EricClaptonDrogaUominiAffari.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("rampolli"),
        StringTextTriggerValue("studi a boston"),
        StringTextTriggerValue("borghesia alta"),
        StringTextTriggerValue("idoli delle mamme"),
        StringTextTriggerValue("figliole")
      ),
      List(
        MediaFile("rphjb_Rampolli.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("capelli corti"),
        StringTextTriggerValue("giacca"),
        StringTextTriggerValue("cravatta"),
        StringTextTriggerValue("passaporto degli stronzi")
      ),
      List(
        MediaFile("rphjb_RocchettariCapelliCortiGiaccaCravattaPassaportoStronzi.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("fregat(a|ura)".r)
      ),
      List(
        MediaFile("rphjb_FregataFregatura.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("\\bmula\\b".r),
        StringTextTriggerValue("storia della mula")
      ),
      List(
        MediaFile("rphjb_Mula.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("si o no")
      ),
      List(
        MediaFile("rphjb_SiONo.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("streghe")
      ),
      List(
        MediaFile("rphjb_Streghe.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("(tornando|andando) (all')?indietro".r),
        StringTextTriggerValue("innovazione")
      ),
      List(
        MediaFile("rphjb_InnovazioneStiamoTornandoIndietro.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("trovamelo")
      ),
      List(
        MediaFile("rphjb_AngeloTrovamelo.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("plettro"),
        StringTextTriggerValue("vicoletto"),
        StringTextTriggerValue("scopata")
      ),
      List(
        MediaFile("rphjb_ChitarraPlettroVicoletto.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("diversi mondi"),
        StringTextTriggerValue("letti sfatti")
      ),
      List(
        MediaFile("rphjb_LettiSfattiDiversiMondi.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("via delle albizzie"),
        StringTextTriggerValue("carpenelli")
      ),
      List(
        MediaFile("rphjb_AlbizziePerlaPioggia.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("ramarro"),
        StringTextTriggerValue("yngwie"),
        StringTextTriggerValue("malmsteen"),
        StringTextTriggerValue("impellitteri")
      ),
      List(
        MediaFile("rphjb_Ramarro.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("vi dovete spaventare")
      ),
      List(
        MediaFile("rphjb_ViDoveteSpaventare.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("amore nello suonare"),
        StringTextTriggerValue("uno freddo"),
        StringTextTriggerValue("buddisti"),
      ),
      List(
        MediaFile("rphjb_AmoreSuonareFreddoBuddistiSchifoso.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("riciclando il (suo )?peggio".r)
      ),
      List(
        MediaFile("rphjb_SteveVaiRiciclando.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("già il titolo"),
        StringTextTriggerValue("coi due punti"),
        RegexTextTriggerValue("re[a]?l illusions".r)
      ),
      List(
        MediaFile("rphjb_RelIllusions.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("trattori"),
        StringTextTriggerValue("palmizio"),
        StringTextTriggerValue("meno c'è"),
        StringTextTriggerValue("meno si rompe")
      ),
      List(
        MediaFile("rphjb_Palmizio.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("peso di un cervello")
      ),
      List(
        MediaFile("rphjb_VitaNemicoCervello.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("cervello pensante"),
        StringTextTriggerValue("questa volta no"),
        StringTextTriggerValue("stupidità incresciosa")
      ),
      List(
        MediaFile("rphjb_CervelloPensante.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("percussionista"),
        StringTextTriggerValue("batterista")
      ),
      List(
        MediaFile("rphjb_CollaSerpeSigarettePercussionista.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("perla di pioggia"),
        StringTextTriggerValue("dove non piove mai")
      ),
      List(
        MediaFile("rphjb_PerlaDiPioggia.mp4"),
        MediaFile("rphjb_AlbizziePerlaPioggia.mp4")
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("madre tortura"),
        RegexTextTriggerValue("(madre )?parrucca".r)
      ),
      List(
        MediaFile("rphjb_MadreTorturaParrucca.mp4")
      ),
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("[l]+[i]+[b]+[e]+[r]+[i]+".r)
      ),
      List(
        MediaFile("rphjb_Liberi.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("\\bcinta\\b".r),
        StringTextTriggerValue("bruce kulick")
      ),
      List(
        MediaFile("rphjb_CintaProblema.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("sepoltura")
      ),
      List(
        MediaFile("rphjb_SepolturaRisata.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("\\bcolla\\b".r),
        StringTextTriggerValue("serpe e serpe")
      ),
      List(
        MediaFile("rphjb_CollaSerpe.mp4"),
        MediaFile("rphjb_CollaSerpeSigarettePercussionista.mp4")
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("big money")
      ),
      List(
        MediaFile("rphjb_BigMoney.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("in cantina")
      ),
      List(
        MediaFile("rphjb_InCantina.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("fregare come un co(gl|j)ione".r),
        RegexTextTriggerValue("Ges[uùù]".r)
      ),
      List(
        MediaFile("rphjb_GesuCoglione.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("con questa tecnica")
      ),
      List(
        MediaFile("rphjb_ConQuestaTecnica.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("(mi|me) so(n|no)? rotto il ca\\b".r),
        StringTextTriggerValue("impazzisce totalmente")
      ),
      List(
        MediaFile("rphjb_RottoIlCa.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("diventare papa")
      ),
      List(
        MediaFile("rphjb_DiventarePapa.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("[cg]hi[td]a[r]+is[td]a pi[uùú] velo[cg]e".r)
      ),
      List(
        MediaFile("rphjb_Arivato.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("\\bbeat\\b".r),
        RegexTextTriggerValue("(e poi[ ,]?[ ]?){2,}".r),
        StringTextTriggerValue("qualche volta vedo lei"),
        StringTextTriggerValue("sfasciavamo tutti gli strumenti"),
      ),
      List(
        MediaFile("rphjb_AssoloBeat.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("buon compleanno")
      ),
      List(
        MediaFile("rphjb_Compleanno.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("ringraziare"),
        StringTextTriggerValue("traffico")
      ),
      List(
        MediaFile("rphjb_RingraziareGianniTraffico.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("(roba|droga) tagliata male".r),
        StringTextTriggerValue("one television"),
        RegexTextTriggerValue("devo fare (un po'|un attimo) (di|de) esercitazione".r)
      ),
      List(
        MediaFile("rphjb_RockMachineIntro.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("poesia")
      ),
      List(
        MediaFile("rphjb_PoesiaMadre.mp4"),
        MediaFile("rphjb_PoesiaRock.mp4"),
        MediaFile("rphjb_Blues.mp4"),
        MediaFile("rphjb_PoesiaMaria.mp4"),
        MediaFile("rphjb_PoesiaArtistiImpiegati.mp4"),
        MediaFile("rphjb_CanzonettePoesieAuschwitzCervello.mp4")
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("w[e]+[l]+[a]+".r)
      ),
      List(
        MediaFile("rphjb_WelaMyFriends.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("blues"),
        StringTextTriggerValue("da piangere"),
      ),
      List(
        MediaFile("rphjb_Blues.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("sabato sera"),
        StringTextTriggerValue("suono sporco")
      ),
      List(
        MediaFile("rphjb_DelirioDelSabatoSera.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("chi tocca (\\w)[,]? muore".r),
        RegexTextTriggerValue("ciao (2001|duemilauno)".r),
        StringTextTriggerValue("marilyn manson")
      ),
      List(
        MediaFile("rphjb_Ciao2001.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("petrucci"),
        RegexTextTriggerValue("capelli (lunghi|corti)".r),
        RegexTextTriggerValue("(impiegato statale|impiegati statali)".r),
      ),
      List(
        MediaFile("rphjb_PetrucciCapelliCorti.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("impiegat[oi]".r),
      ),
      List(
        MediaFile("rphjb_PetrucciCapelliCorti.mp4"),
        MediaFile("rphjb_PoesiaArtistiImpiegati.mp4")
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("progressive"),
        StringTextTriggerValue("regressive"),
        StringTextTriggerValue("i genesis")
      ),
      List(
        MediaFile("rphjb_Regressive.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("cresta dell'onda"),
        StringTextTriggerValue("orlo del crollo"),
      ),
      List(
        MediaFile("rphjb_CrestaOnda.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("stronzo")
      ),
      List(
        MediaFile("rphjb_StronzoFiglioMignotta.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("biscione"),
        StringTextTriggerValue("i piatti"),
      ),
      List(
        MediaFile("rphjb_BiscionePiatti.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("platinette"),
        StringTextTriggerValue("due persone in una"),
        StringTextTriggerValue("quando scopo me la levo"),
        StringTextTriggerValue("il mio sbadiglio"),
        StringTextTriggerValue("solo per un taglio"),
        StringTextTriggerValue("labbro superiore")
      ),
      List(
        MediaFile("rphjb_PlatinetteLabbroSuperiore.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("non aprite quella porta")
      ),
      List(
        MediaFile("rphjb_NonApriteQuellaPorta.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("paralitico")
      ),
      List(
        MediaFile("rphjb_DanzaMacabra.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("mettetevi in ginocchio"),
        StringTextTriggerValue("nuovo messia")
      ),
      List(
        MediaFile("rphjb_MetteteviInGinocchio.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("sigarett[ea]".r)
      ),
      List(
        MediaFile("rphjb_Sigarette.mp4"),
        MediaFile("rphjb_CollaSerpeSigarettePercussionista.mp4")
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("orecchie sensibili"),
        StringTextTriggerValue("rumore delle lacrime")
      ),
      List(
        MediaFile("rphjb_OrecchieSensibiliRumoreLacrime.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("sapere tutto"),
        StringTextTriggerValue("se non le sai le cose"),
        StringTextTriggerValue("jordan rudess"),
        StringTextTriggerValue("radio rock"),
        StringTextTriggerValue("informazioni sbagliate")
      ),
      List(
        MediaFile("rphjb_RadioRockErrori.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("uccidere")
      ),
      List(
        MediaFile("rphjb_UccidereUnaPersona.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("distruggere il proprio sesso"),
        StringTextTriggerValue("ammaestrare il dolore")
      ),
      List(
        MediaFile("rphjb_AmmaestrareIlDolore.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("insegnante di [cg]hi[dt]arra".r)
      ),
      List(
        MediaFile("rphjb_InsegnanteDiChitarraModerna.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("pellegrinaggio"),
        StringTextTriggerValue("simposio del metallo"),
        StringTextTriggerValue("istinti musicali"),
      ),
      List(
        MediaFile("rphjb_PellegrinaggioSimposioMetallo.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("ridicoli")
      ),
      List(
        MediaFile("rphjb_Ridicoli.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("l'unico bravo"),
        RegexTextTriggerValue("scarica d(i |')andrenalina".r),
        RegexTextTriggerValue("non valgono (un cazzo|niente)".r),
      ),
      List(
        MediaFile("rphjb_UnicoBravo.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("come mi aiuta"),
      ),
      List(
        MediaFile("rphjb_DubbioComeMiAiuta.mp4")
      )
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("\\bdubbio\\b".r)
      ),
      List(
        MediaFile("rphjb_DubbioComeMiAiuta.mp4"),
        MediaFile("rphjb_DubbioScantinatiGiocoRattoGatto.mp4"),
      ),
      replySelection = RandomSelection
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("scantinati"),
        RegexTextTriggerValue("gioco (io )? del gatto e (voi )? del (ratto|topo)".r)
      ),
      List(MediaFile("rphjb_DubbioScantinatiGiocoRattoGatto.mp4"))
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("londra")
      ),
      List(MediaFile("rphjb_Londra.mp4"))
    ),
    ReplyBundleMessage(
      TextTrigger(
        StringTextTriggerValue("latte droga"),
        StringTextTriggerValue("solo gregge"),
        StringTextTriggerValue("gregge da discoteca"),
        StringTextTriggerValue("sputo in un bicchiere"),
      ),
      List(MediaFile("rphjb_PoveriIgnorantiLatteDrogaSoloGreggeSputo.mp4"))
    ),
    ReplyBundleMessage(
      TextTrigger(
        RegexTextTriggerValue("drogh[ae] (legger[ae]|pesant[ei])".r),
        StringTextTriggerValue("ammoniaca"),
        StringTextTriggerValue("veleno per topi"),
        StringTextTriggerValue("borotalco")
      ),
      List(MediaFile("rphjb_DrogheLeggere.mp4"))
    ),
    ReplyBundleMessage(TextTrigger(StringTextTriggerValue("peggio cose")), List(MediaFile("rphjb_Venerdi.mp4")))
  )

}