# == Schema Information
#
# Table name: keyboard_variants
#
#  id          :integer          not null, primary key
#  name        :string(255)
#  type        :integer
#  created_at  :datetime
#  updated_at  :datetime
#  keyboard_id :integer
#

class KeyboardVariant < ActiveRecord::Base
	has_many :key_positions
	belongs_to :keyboard
  belongs_to :keyboard_type
end
