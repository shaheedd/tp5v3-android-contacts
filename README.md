# TP5 Android — Contacts V3 (Fragments, RecyclerView, Maps)

Application Android Kotlin (interfaces XML) qui fait évoluer la V2 vers une
architecture à **une seule activité et plusieurs fragments dynamiques**.

## Fonctionnalités

- **MainActivity** : hôte unique, `FragmentContainerView`, transactions
  `replace()` + `addToBackStack()`, rotation gérée sans empiler de fragments en double.
- **ListeContactsFragment** : `RecyclerView` personnalisée (`ContactAdapter` +
  `ViewHolder`) remplaçant la `ListView`. Recherche instantanée insensible à la casse
  sur nom, prénom et téléphone ; message « aucun résultat » ; clic sûr grâce à un
  identifiant de contact stable.
- **DetailContactFragment** : consultation/modification du contact, photo (galerie
  et caméra via `FileProvider`), actions **Appeler** (`ACTION_DIAL`) et
  **Envoyer un e-mail** (`ACTION_SEND` + `message/rfc822`), coordonnées GPS avec
  validation (-90..90 / -180..180), **Enregistrer** et **Annuler**.
- **CarteContactFragment** : SDK **Google Maps** (`SupportMapFragment`, centrage et
  marqueur portant le nom) ; si aucune clé n'est configurée localement, une vue
  cartographique de repli affiche le marqueur afin de garder l'application utilisable.
- Communication entre fragments : `Bundle` avec l'identifiant du contact +
  **Fragment Result API** pour actualiser la liste après enregistrement.

## Configuration de la clé Google Maps

La clé n'est **jamais** publiée dans le dépôt. Ajoutez-la localement dans
`local.properties` (fichier exclu par `.gitignore`) :

```properties
maps.api.key=VOTRE_CLE_GOOGLE_MAPS
```

Le build l'injecte dans `BuildConfig.MAPS_API_KEY` et dans le manifeste via un
`manifestPlaceholder`. Sans clé, l'application utilise la vue de repli.

## Structure

| Fichier | Rôle |
| --- | --- |
| `MainActivity.kt` | Hôte des fragments et pile de navigation |
| `ListeContactsFragment.kt` | Liste, filtrage, ouverture du détail |
| `ContactAdapter.kt` | Adapter `RecyclerView` + `ViewHolder` |
| `DetailContactFragment.kt` | Édition, photos, intents, coordonnées, sauvegarde |
| `CarteContactFragment.kt` | Carte Google Maps ou vue de repli |
| `MapFallbackView.kt` | Carte de démonstration sans clé API |
| `ContactRepository.kt` | Collection en mémoire des contacts |
| `captures/` | Captures d'écran réelles |
| `compte-rendu/` | Compte rendu PDF |

## Compilation

```powershell
.\gradlew assembleDebug
```

`compileSdk` 37 (seule plateforme installée localement), `minSdk` 24,
`targetSdk` 35, Java 17.

## Dépôt

https://github.com/shaheedd/tp5v3-android-contacts
