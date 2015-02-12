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
	has_many :key_positions, dependent: :destroy
	belongs_to :keyboard
  belongs_to :keyboard_type

	def key_position_rows

		ordered_key_positions = KeyPosition.where(keyboard_variant_id: self.id).order(row_index: :asc, column_index: :asc)

		key_position_rows = Array.new
		key_position_columns = Array.new

		row_index = ordered_key_positions.first.row_index
		column_index = ordered_key_positions.first.column_index

		ordered_key_positions.each_with_index do |key_position, index|
			if key_position.row_index == row_index
				key_position_columns.push(key_position)
			else
				key_position_rows.push(key_position_columns)
				key_position_columns = Array.new
				row_index = key_position.row_index
        key_position_columns.push(key_position)
			end
			if index == ordered_key_positions.size - 1
				key_position_rows.push(key_position_columns)
			end
		end

		key_position_rows

	end

	def created_at_epoch
		created_at.to_f
	end

	def updated_at_epoch
		updated_at.to_f
	end

end
