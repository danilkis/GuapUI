import Foundation

import SwiftUI

struct ExpandableGroupField: View {
    let items: [Group]
    let label: String
    @Binding var selectedGroupId: Int

    @State private var isExpanded = false
    @State private var selectedValue = ""

    var body: some View {
        Menu {
            ForEach(items) { group in
                Button(action: {
                    selectedValue = group.name
                    selectedGroupId = group.id
                    isExpanded = false
                }) {
                    Text(group.name)
                }
            }
        } label: {
            HStack {
                Text(selectedValue.isEmpty ? label : selectedValue)
                Spacer()
                Image(systemName: "chevron.down")
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(8)
        }
    }
}