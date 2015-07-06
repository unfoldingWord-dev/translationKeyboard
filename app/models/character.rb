# == Schema Information
#
# Table name: characters
#
#  id              :integer          not null, primary key
#  modmask         :integer
#  sortnumber      :integer
#  created_at      :datetime
#  updated_at      :datetime
#  key_position_id :integer
#

class Character < ActiveRecord::Base
	belongs_to :key_position
  belongs_to :unicode_character

  has_many :character_unicode_mapping

  def utf8hex
    # unicode_character.utf8hex.to_s(16)
    hex_code = ''
    characters = character_unicode_mapping
    characters.each do |char|
      if char == characters.last
        hex_code += UnicodeCharacter.find(char.unicode_character_id).utf8hex.to_s(16)
      else
        hex_code += UnicodeCharacter.find(char.unicode_character_id).utf8hex.to_s(16)
        hex_code += ';'
      end
    end
    hex_code
  end

  def unicode_int_value
    unicode_character.utf8hex.to_i
  end

  def unicode_array_value
    characters = character_unicode_mapping
    unicode_array = []
    characters.each do |char|
      unicode_char = UnicodeCharacter.find(char.unicode_character_id).utf8hex.to_i
      unicode_array.push(unicode_char)
    end
    unicode_array
  end

  def unicode_value
    characters = character_unicode_mapping.limit(1)
    unicode_char = ''
    characters.each do |char|
      unicode_char = UnicodeCharacter.find(char.unicode_character_id).utf8hex.to_i
    end
    unicode_char
  end

  def character_char
    unicode_character.utf8hex.chr
  end

  def uft8HexHtml
    # hexedInt = unicode_character.utf8hex.to_s(16)
    # "&\#x#{hexedInt};".html_safe
    characters = character_unicode_mapping
    hex_char = ''
    char_array = []
    characters.each do |char|
      hexedInt = UnicodeCharacter.find(char.unicode_character_id).utf8hex.to_s(16)
      char_string = "&\#x#{hexedInt};".html_safe
      if char == characters.first
        hex_char = char_string
      else
        hex_char += char_string
      end
    end
    hex_char
  end

end
