#

attributes :percent_width
#attributes :row_index, :column_index

child :characters do
    attributes  :modmask
    attributes :unicode_array_value => :unicode
end