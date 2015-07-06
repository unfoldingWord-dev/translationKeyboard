class CharacterUnicodeMapping < ActiveRecord::Base
  belongs_to :character
  belongs_to :unicode_character
end
