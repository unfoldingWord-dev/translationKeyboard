# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)

keyboardTypes = KeyboardType.create([{name: 'Android 4 Row'}, {name: 'Android 5 Row'}])

aKeyboard = Keyboard.create([{name: 'English (en)'}])

aKeyboardVariant = KeyboardVariant.create([{keyboard: aKeyboard.first, keyboard_type: keyboardTypes.first, name: "English (en-US) Android 4 Row" }])

# The ~ Key
tildaKeyPosition = KeyPosition.create([{column_index: 0, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
graveAccentKey = UnicodeCharacter.create([{englishDesc: "Grave Accent",utf8hex:'60'.hex,}])
tildaKey = UnicodeCharacter.create([{englishDesc: "Tilda",utf8hex:'7E'.hex,}])
tildaKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:graveAccentKey.first, key_position:tildaKeyPosition.first},
                                 {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:tildaKey.first, key_position:tildaKeyPosition.first}])

# The Digit 1 Key
oneKeyPosition = KeyPosition.create([{column_index: 1, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
oneKey = UnicodeCharacter.create([{englishDesc: "Digit one",utf8hex:'31'.hex,}])
exclamationKey = UnicodeCharacter.create([{englishDesc: "Exclamation Mark",utf8hex:'21'.hex,}])
upsideDownExclamationKey = UnicodeCharacter.create([{englishDesc: "Inverted Exclamation Mark",utf8hex:'A1'.hex,}])
oneKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:oneKey.first, key_position:oneKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:exclamationKey.first, key_position:oneKeyPosition.first},
                                  {modmask: '10'.to_i(2),  sortnumber: 1, unicode_character:upsideDownExclamationKey.first, key_position:oneKeyPosition.first}] )

# The 2 Key
twoKeyPosition = KeyPosition.create([{column_index: 2, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
twoKey = UnicodeCharacter.create([{englishDesc: "Digit two",utf8hex:'32'.hex,}])
atSymbolKey = UnicodeCharacter.create([{englishDesc: "Commercial at",utf8hex:'40'.hex,}])
twoKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:twoKey.first, key_position:twoKeyPosition.first},
                                {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:atSymbolKey.first, key_position:twoKeyPosition.first}])

# The 3 Key
threeKeyPosition = KeyPosition.create([{column_index: 3, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
threeKey = UnicodeCharacter.create([{englishDesc: "Digit three",utf8hex:'33'.hex,}])
numberSignKey = UnicodeCharacter.create([{englishDesc: "Number sign",utf8hex:'23'.hex,}])
threeKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:threeKey.first, key_position:threeKeyPosition.first},
                                {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:numberSignKey.first, key_position:threeKeyPosition.first}])

# The 4 Key
fourKeyPosition = KeyPosition.create([{column_index: 4, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
fourKey = UnicodeCharacter.create([{englishDesc: "Digit four",utf8hex:'34'.hex,}])
dollarSignKey = UnicodeCharacter.create([{englishDesc: "Dollar Sign",utf8hex:'24'.hex,}])
fourKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:fourKey.first, key_position:fourKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:dollarSignKey.first, key_position:fourKeyPosition.first}])

# The 5 Key
fiveKeyPosition = KeyPosition.create([{column_index: 5, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
fiveKey = UnicodeCharacter.create([{englishDesc: "Digit five",utf8hex:'35'.hex,}])
percentSignKey = UnicodeCharacter.create([{englishDesc: "Percent Sign",utf8hex:'25'.hex,}])
fiveKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:fiveKey.first, key_position:fiveKeyPosition.first},
                                 {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:percentSignKey.first, key_position:fiveKeyPosition.first}])

# The 6 Key
sixKeyPosition = KeyPosition.create([{column_index: 6, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
sixKey = UnicodeCharacter.create([{englishDesc: "Digit six",utf8hex:'36'.hex,}])
circumflexAccentKey = UnicodeCharacter.create([{englishDesc: "Circumflex accent",utf8hex:'5E'.hex,}])
sixKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:sixKey.first, key_position:sixKeyPosition.first},
                                 {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:circumflexAccentKey.first, key_position:sixKeyPosition.first}])

# The 7 Key
sevenKeyPosition = KeyPosition.create([{column_index: 7, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
sevenKey = UnicodeCharacter.create([{englishDesc: "Digit seven",utf8hex:'37'.hex,}])
ampersandKey = UnicodeCharacter.create([{englishDesc: "Ampersand",utf8hex:'26'.hex,}])
sevenKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:sevenKey.first, key_position:sevenKeyPosition.first},
                                {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:ampersandKey.first, key_position:sevenKeyPosition.first}])

# The 8 Key
eightKeyPosition = KeyPosition.create([{column_index: 8, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
eightKey = UnicodeCharacter.create([{englishDesc: "Digit eight",utf8hex:'38'.hex,}])
asteriskKey = UnicodeCharacter.create([{englishDesc: "Asterisk",utf8hex:'2A'.hex,}])
eightKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:eightKey.first, key_position:eightKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:asteriskKey.first, key_position:eightKeyPosition.first}])

# The 9 Key
nineKeyPosition = KeyPosition.create([{column_index: 9, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
nineKey = UnicodeCharacter.create([{englishDesc: "Digit nine",utf8hex:'39'.hex,}])
leftParenthesisKey = UnicodeCharacter.create([{englishDesc: "Left parenthesis",utf8hex:'28'.hex,}])
nineKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:nineKey.first, key_position:nineKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:leftParenthesisKey.first, key_position:nineKeyPosition.first}])

# The 0 Key
zeroKeyPosition = KeyPosition.create([{column_index: 10, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
zeroKey = UnicodeCharacter.create([{englishDesc: "Digit zero",utf8hex:'30'.hex,}])
rightParenthesisKey = UnicodeCharacter.create([{englishDesc: "Right parenthesis",utf8hex:'29'.hex,}])
zeroKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:zeroKey.first, key_position:zeroKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:rightParenthesisKey.first, key_position:zeroKeyPosition.first}])

# The - Key
minusKeyPosition = KeyPosition.create([{column_index: 11, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
minusKey = UnicodeCharacter.create([{englishDesc: "Hyphen-minus",utf8hex:'2D'.hex,}])
lowLineKey = UnicodeCharacter.create([{englishDesc: "Low line",utf8hex:'5F'.hex,}])
minusKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:minusKey.first, key_position:minusKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:lowLineKey.first, key_position:minusKeyPosition.first}])

# The equals Key
equalsKeyPosition = KeyPosition.create([{column_index: 12, row_index:0, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
equalsKey = UnicodeCharacter.create([{englishDesc: "Equals sign",utf8hex:'3D'.hex,}])
plusSignKey = UnicodeCharacter.create([{englishDesc: "Plus sign",utf8hex:'2B'.hex,}])
equalsKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:equalsKey.first, key_position:equalsKeyPosition.first},
                                  {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:plusSignKey.first, key_position:equalsKeyPosition.first}])

# The q Key
qKeyPosition = KeyPosition.create([{column_index: 0, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
qKey = UnicodeCharacter.create([{englishDesc: "Latin small letter q",utf8hex:'71'.hex,}])
qCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter q",utf8hex:'51'.hex,}])
qKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:qKey.first, key_position:qKeyPosition.first},
                                   {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:qCapitalKey.first, key_position:qKeyPosition.first}])

# The w Key
wKeyPosition = KeyPosition.create([{column_index: 1, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
wKey = UnicodeCharacter.create([{englishDesc: "Latin small letter w",utf8hex:'77'.hex,}])
wCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter w",utf8hex:'57'.hex,}])
wKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:wKey.first, key_position:wKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:wCapitalKey.first, key_position:wKeyPosition.first}])

# The e Key
eKeyPosition = KeyPosition.create([{column_index: 2, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
eKey = UnicodeCharacter.create([{englishDesc: "Latin small letter e",utf8hex:'65'.hex,}])
eCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter e",utf8hex:'45'.hex,}])
eKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:eKey.first, key_position:eKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:eCapitalKey.first, key_position:eKeyPosition.first}])

# The r Key
rKeyPosition = KeyPosition.create([{column_index: 3, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
rKey = UnicodeCharacter.create([{englishDesc: "Latin small letter r",utf8hex:'72'.hex,}])
rCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter r",utf8hex:'52'.hex,}])
rKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:rKey.first, key_position:rKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:rCapitalKey.first, key_position:rKeyPosition.first}])

# The t Key
tKeyPosition = KeyPosition.create([{column_index: 4, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
tKey = UnicodeCharacter.create([{englishDesc: "Latin small letter t",utf8hex:'74'.hex,}])
tCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter t",utf8hex:'54'.hex,}])
tKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:tKey.first, key_position:tKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:tCapitalKey.first, key_position:tKeyPosition.first}])

# The y Key
yKeyPosition = KeyPosition.create([{column_index: 5, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
yKey = UnicodeCharacter.create([{englishDesc: "Latin small letter y",utf8hex:'79'.hex,}])
yCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter y",utf8hex:'59'.hex,}])
yKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:yKey.first, key_position:yKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:yCapitalKey.first, key_position:yKeyPosition.first}])

# The u Key
uKeyPosition = KeyPosition.create([{column_index: 6, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
uKey = UnicodeCharacter.create([{englishDesc: "Latin small letter u",utf8hex:'75'.hex,}])
uCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter u",utf8hex:'55'.hex,}])
uKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:uKey.first, key_position:uKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:uCapitalKey.first, key_position:uKeyPosition.first}])

# The i Key
iKeyPosition = KeyPosition.create([{column_index: 7, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
iKey = UnicodeCharacter.create([{englishDesc: "Latin small letter i",utf8hex:'69'.hex,}])
iCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter i",utf8hex:'49'.hex,}])
iKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:iKey.first, key_position:iKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:iCapitalKey.first, key_position:iKeyPosition.first}])

# The o Key
oKeyPosition = KeyPosition.create([{column_index: 8, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
oKey = UnicodeCharacter.create([{englishDesc: "Latin small letter o",utf8hex:'6F'.hex,}])
oCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter o",utf8hex:'4F'.hex,}])
oKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:oKey.first, key_position:oKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:oCapitalKey.first, key_position:oKeyPosition.first}])

# The p Key
pKeyPosition = KeyPosition.create([{column_index: 9, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
pKey = UnicodeCharacter.create([{englishDesc: "Latin small letter p",utf8hex:'70'.hex,}])
pCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter p",utf8hex:'50'.hex,}])
pKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:pKey.first, key_position:pKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:pCapitalKey.first, key_position:pKeyPosition.first}])

# The [ Key
leftSquareBracketKeyPosition = KeyPosition.create([{column_index: 10, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
leftSquareBracketKey = UnicodeCharacter.create([{englishDesc: "Left square bracket",utf8hex:'5B'.hex,}])
leftCurlyBracketKey = UnicodeCharacter.create([{englishDesc: "Left curly bracket",utf8hex:'7B'.hex,}])
leftSquareBracketKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:leftSquareBracketKey.first, key_position:leftSquareBracketKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:leftCurlyBracketKey.first, key_position:leftSquareBracketKeyPosition.first}])

# The ] Key
rightSquareBracketKeyPosition = KeyPosition.create([{column_index: 11, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
rightSquareBracketKey = UnicodeCharacter.create([{englishDesc: "Right square bracket",utf8hex:'5D'.hex,}])
rightCurlyBracketKey = UnicodeCharacter.create([{englishDesc: "Right curly bracket",utf8hex:'7D'.hex,}])
rightSquareBracketKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:rightSquareBracketKey.first, key_position:rightSquareBracketKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:rightCurlyBracketKey.first, key_position:rightSquareBracketKeyPosition.first}])

# The \ Key
backSlashKeyPosition = KeyPosition.create([{column_index: 12, row_index:1, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
backSlashKey = UnicodeCharacter.create([{englishDesc: "Reverse solidus",utf8hex:'5C'.hex,}])
pipeKey = UnicodeCharacter.create([{englishDesc: "Vertical line",utf8hex:'7C'.hex,}])
backSlashKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:backSlashKey.first, key_position:backSlashKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:pipeKey.first, key_position:backSlashKeyPosition.first}])

# The a Key
aKeyPosition = KeyPosition.create([{column_index: 0, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
aKey = UnicodeCharacter.create([{englishDesc: "Latin small letter a",utf8hex:'61'.hex,}])
aCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter a",utf8hex:'41'.hex,}])
aKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:aKey.first, key_position:aKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:aCapitalKey.first, key_position:aKeyPosition.first}])

# The s Key
sKeyPosition = KeyPosition.create([{column_index: 1, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
sKey = UnicodeCharacter.create([{englishDesc: "Latin small letter s",utf8hex:'73'.hex,}])
sCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter s",utf8hex:'53'.hex,}])
sKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:sKey.first, key_position:sKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:sCapitalKey.first, key_position:sKeyPosition.first}])

# The d Key
dKeyPosition = KeyPosition.create([{column_index: 2, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
dKey = UnicodeCharacter.create([{englishDesc: "Latin small letter d",utf8hex:'64'.hex,}])
dCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter d",utf8hex:'44'.hex,}])
dKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:dKey.first, key_position:dKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:dCapitalKey.first, key_position:dKeyPosition.first}])

# The f Key
fKeyPosition = KeyPosition.create([{column_index: 3, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
fKey = UnicodeCharacter.create([{englishDesc: "Latin small letter f",utf8hex:'66'.hex,}])
fCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter f",utf8hex:'46'.hex,}])
fKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:fKey.first, key_position:fKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:fCapitalKey.first, key_position:fKeyPosition.first}])

# The g Key
gKeyPosition = KeyPosition.create([{column_index: 4, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
gKey = UnicodeCharacter.create([{englishDesc: "Latin small letter g",utf8hex:'67'.hex,}])
gCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter g",utf8hex:'47'.hex,}])
gKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:gKey.first, key_position:gKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:gCapitalKey.first, key_position:gKeyPosition.first}])

# The h Key
hKeyPosition = KeyPosition.create([{column_index: 5, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
hKey = UnicodeCharacter.create([{englishDesc: "Latin small letter h",utf8hex:'68'.hex,}])
hCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter h",utf8hex:'48'.hex,}])
hKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:hKey.first, key_position:hKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:hCapitalKey.first, key_position:hKeyPosition.first}])

# The j Key
jKeyPosition = KeyPosition.create([{column_index: 6, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
jKey = UnicodeCharacter.create([{englishDesc: "Latin small letter j",utf8hex:'6A'.hex,}])
jCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter j",utf8hex:'4A'.hex,}])
jKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:jKey.first, key_position:jKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:jCapitalKey.first, key_position:jKeyPosition.first}])

# The k Key
kKeyPosition = KeyPosition.create([{column_index: 7, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
kKey = UnicodeCharacter.create([{englishDesc: "Latin small letter k",utf8hex:'6B'.hex,}])
kCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter k",utf8hex:'4B'.hex,}])
kKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:kKey.first, key_position:kKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:kCapitalKey.first, key_position:kKeyPosition.first}])

# The l Key
lKeyPosition = KeyPosition.create([{column_index: 8, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
lKey = UnicodeCharacter.create([{englishDesc: "Latin small letter l",utf8hex:'6C'.hex,}])
lCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter l",utf8hex:'4C'.hex,}])
lKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:lKey.first, key_position:lKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:lCapitalKey.first, key_position:lKeyPosition.first}])

# The ; Key
semiColonKeyPosition = KeyPosition.create([{column_index: 9, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
semiColonKey = UnicodeCharacter.create([{englishDesc: "Semicolon",utf8hex:'3B'.hex,}])
colonKey = UnicodeCharacter.create([{englishDesc: "Colon",utf8hex:'4A'.hex,}])
semiColonKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:semiColonKey.first, key_position:semiColonKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:colonKey.first, key_position:semiColonKeyPosition.first}])

# The ' Key
apostropheKeyPosition = KeyPosition.create([{column_index: 10, row_index:2, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
apostropheKey = UnicodeCharacter.create([{englishDesc: "Apostrophe",utf8hex:'27'.hex,}])
quotationMarkKey = UnicodeCharacter.create([{englishDesc: "Quotation mark",utf8hex:'2'.hex,}])
apostropheKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:apostropheKey.first, key_position:apostropheKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:quotationMarkKey.first, key_position:apostropheKeyPosition.first}])

# The z Key
zKeyPosition = KeyPosition.create([{column_index: 0, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
zKey = UnicodeCharacter.create([{englishDesc: "Latin small letter z",utf8hex:'7A'.hex,}])
zCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter z",utf8hex:'5A'.hex,}])
zKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:zKey.first, key_position:zKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:zCapitalKey.first, key_position:zKeyPosition.first}])

# The x Key
xKeyPosition = KeyPosition.create([{column_index: 1, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
xKey = UnicodeCharacter.create([{englishDesc: "Latin small letter x",utf8hex:'78'.hex,}])
xCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter x",utf8hex:'58'.hex,}])
xKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:xKey.first, key_position:xKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:xCapitalKey.first, key_position:xKeyPosition.first}])

# The c Key
cKeyPosition = KeyPosition.create([{column_index: 2, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
cKey = UnicodeCharacter.create([{englishDesc: "Latin small letter c",utf8hex:'63'.hex,}])
cCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter c",utf8hex:'43'.hex,}])
cKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:cKey.first, key_position:cKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:cCapitalKey.first, key_position:cKeyPosition.first}])

# The v Key
vKeyPosition = KeyPosition.create([{column_index: 3, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
vKey = UnicodeCharacter.create([{englishDesc: "Latin small letter v",utf8hex:'76'.hex,}])
vCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter v",utf8hex:'56'.hex,}])
vKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:vKey.first, key_position:vKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:vCapitalKey.first, key_position:vKeyPosition.first}])

# The b Key
bKeyPosition = KeyPosition.create([{column_index: 4, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
bKey = UnicodeCharacter.create([{englishDesc: "Latin small letter b",utf8hex:'62'.hex,}])
bCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter b",utf8hex:'42'.hex,}])
bKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:bKey.first, key_position:bKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:bCapitalKey.first, key_position:bKeyPosition.first}])

# The n Key
nKeyPosition = KeyPosition.create([{column_index: 5, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
nKey = UnicodeCharacter.create([{englishDesc: "Latin small letter n",utf8hex:'6E'.hex,}])
nCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter n",utf8hex:'4E'.hex,}])
nKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:nKey.first, key_position:nKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:nCapitalKey.first, key_position:nKeyPosition.first}])

# The m Key
mKeyPosition = KeyPosition.create([{column_index: 6, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
mKey = UnicodeCharacter.create([{englishDesc: "Latin small letter m",utf8hex:'6D'.hex,}])
mCapitalKey = UnicodeCharacter.create([{englishDesc: "Latin capital letter m",utf8hex:'4D'.hex,}])
mKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:mKey.first, key_position:mKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:mCapitalKey.first, key_position:mKeyPosition.first}])

# The , Key
commaKeyPosition = KeyPosition.create([{column_index: 7, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
commaKey = UnicodeCharacter.create([{englishDesc: "Comma",utf8hex:'2C'.hex,}])
lessThanKey = UnicodeCharacter.create([{englishDesc: "Less-than sign",utf8hex:'3C'.hex,}])
commaKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:commaKey.first, key_position:commaKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:lessThanKey.first, key_position:commaKeyPosition.first}])

# The . Key
periodKeyPosition = KeyPosition.create([{column_index: 8, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
periodKey = UnicodeCharacter.create([{englishDesc: "Full stop",utf8hex:'2E'.hex,}])
greaterThanKey = UnicodeCharacter.create([{englishDesc: "Greater-than sign",utf8hex:'3E'.hex,}])
periodKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:periodKey.first, key_position:periodKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:greaterThanKey.first, key_position:periodKeyPosition.first}])

# The / Key
forwardSlashKeyPosition = KeyPosition.create([{column_index: 9, row_index:3, percent_width:1, keyboard_variant:aKeyboardVariant.first}])
forwardSlashKey = UnicodeCharacter.create([{englishDesc: "Solidus",utf8hex:'2F'.hex,}])
questionMarkKey = UnicodeCharacter.create([{englishDesc: "Question mark",utf8hex:'3F'.hex,}])
forwardSlashKeyChars = Character.create([{modmask: '0'.to_i(2),  sortnumber: 1, unicode_character:forwardSlashKey.first, key_position:forwardSlashKeyPosition.first},
    {modmask: '1'.to_i(2),  sortnumber: 1, unicode_character:questionMarkKey.first, key_position:forwardSlashKeyPosition.first}])