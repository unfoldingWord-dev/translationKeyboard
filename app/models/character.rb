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

  def utf8hex
    unicode_character.utf8hex.to_s(16)
  end

  def character_char
    unicode_character.utf8hex.chr
  end

  def uft8HexHtml
    hexedInt = unicode_character.utf8hex.to_s(16)
    "&\#x#{hexedInt};".html_safe
  end

end
